#include <torch/script.h>
#include <iostream>
#include <memory>


/*** 0代表Wake,1代表N1,2代表N2,3代表N3,4代表REM ***/
int* stage_torch_index;    // 存储该模型得到的结果（睡眠阶段）,数组大小：数据有多少个30s
float** stage_torch_prob;   // 这个是概率大小,维度：[数据有多少个30s][5(表示5个阶段的概率)], 5个阶段依次为: wake/n1/n2/n3/rem

struct myHeader	// 不知道原来的Head是啥，应该是这个形式吧：
{
	unsigned int ID_electrode[10] = { 0,1,2,3,4,5,6,7,8,9 };
	unsigned int frequency[5] = { 100,100,100,100,100 };
	unsigned int nrecords = 1000;
	unsigned int duration = 30;
}header;

/***
* @param in_data: 输入
* @param out_data: 输出
* @param old: 原始数据频率
* function: 重采样至125Hz用于放进模型（后期会训练100Hz版的模型）
*/
void resample(float* in_data, float* out_data, float old)
{
	float rio = old / 125;
	int down, up, coef;
	for (int i = 0; i < 30 * 125; i++)
	{
		down = int(i * rio);
		if (down == (i * rio))
		{
			up = down;
		}
		else
		{
			up = down + 1;
		}
		if (up == old * 30)
		{
			out_data[i] = in_data[down];  // 上采样时，采样前信号会少一个采样点，因此直接赋予最后一个采样值
			continue;
		}
		coef = i * rio - down;
		if (in_data[down] <= in_data[up])
		{
			out_data[i] = in_data[down] + (in_data[up] - in_data[down]) * coef;
		}
		else
		{
			out_data[i] = in_data[down] - (in_data[down] - in_data[up]) * coef;
		}
	}
}


/***
* @param data: shape=[n, 5, 3750]，5个通道依次为C3,C4,EOGL,EOGR,EMG
* function: 对应原程序里的tensorflow_process_xxx
***/
void torch_process(float*** data)
{
	/* ???
	// 这里数值的部分不用关心，大概就是计算数据的长度，有多少s，因为他们要求说用户可以修改开始结束的位置，所以要分情况算一下
	get_time(0, 100);
	QTime t1 = QTime(hour, minite, second);    // 原来的开始时间
	get_time(num_stage * 3000, 100);
	QTime t2 = QTime(hour, minite, second);    // 原来的结束时间
	QTime t3 = ui->timeEdit_StartTime->time(); // 当前选择的开始时间
	QTime t4 = ui->timeEdit_EndTime->time();   // 当前选择的结束时间
	int k = distance_Of_Time(t1, t2);      // 表示总数据长度为多少秒

	// 当前所选数据的总长度(s)
	int length_Now = 0;
	if(flag_TimeChanged == 0){
		length_Now = k;
	}
	else{
		length_Now = gap1;}
	*/
	int length_Now = 1000 * 30;	// 表示总数据长度为多少秒

	// load torch module
	using torch::jit::script::Module;
	Module module = torch::jit::load(".\\cpu120_tinymodel.pt");
	module.eval();

	// 输入占位符
	int number = int(length_Now / 30);	// epoch数
	torch::Tensor input_tensor = torch::zeros({ number, 5, 3750, 5 }).toType(torch::kFloat);

	// 信号输入
	auto data_tensor = torch::empty({ number, 5, 3750 }, torch::kFloat);
	float* data_tensor_items = data_tensor.data<float>();
	for (int i = 0; i < number; i++) {
		for (int j = 0; j < 5; j++) {
			for (int k = 0; k < 3750; k++) {
				*data_tensor_items++ = data[i][j][k];
			}
		}
	}
	// shape to [n_epoch, 3750, 5]
	data_tensor = data_tensor.permute({ 0,2,1 });
	// head and tail
	input_tensor[0] = torch::cat({ data_tensor.slice(0,0,2),data_tensor.slice(0,0,3) }, 0);
	input_tensor[1] = torch::cat({ data_tensor.slice(0,0,1),data_tensor.slice(0,0,4) }, 0);
	input_tensor[number - 1] = torch::cat({ data_tensor.slice(0,number - 3,number), data_tensor.slice(0,0,2) }, 0);
	input_tensor[number - 2] = torch::cat({ data_tensor.slice(0,number - 4,number), data_tensor.slice(0,0,1) }, 0);
	// body
	for (int i = 2; i < number - 2; i++)
		input_tensor[i] = data_tensor.slice(0, i - 2, i + 3);

	// 模型推导
	at::Tensor output;
	int n_batch = 100;	//一次只判100帧来节省tensor占的内存
	if (n_batch == 0) {
		output = torch::ones({ number, 6 }, torch::kFloat);
		std::vector<torch::jit::IValue> inputs;
		inputs.push_back(input_tensor);
		output = module.forward(inputs).toTensor();	//这个output特别占内存，暂时没有好办法，只能用batch
	}
	else {
		output = torch::empty({ number, 6 }, torch::kFloat);
		for (int i = 0; i < number; i += n_batch) {
			int end = i + n_batch < number ? i + n_batch : number;
			std::vector<torch::jit::IValue> inputs;
			inputs.push_back(input_tensor.slice(0, i, end));
			at::Tensor out_batch = module.forward(inputs).toTensor();
			for (int j = i; j < end; j++) {
				for (int k = 0; k < 6; k++) {
					output[j][k] = out_batch[j - i][k].item().toFloat();
				}
			}
		}
	}

	// 后处理
	output = torch::reshape(output, { -1, 6 });
	output = output.slice(1, 0, 5);
	torch::Tensor stage = torch::argmax(output, 1).to(torch::kByte);	//阶段划分结果

	// 输出结果
	stage_torch_index = new int[number];		// 存储该模型得到的结果（睡眠阶段）,数组大小：数据有多少个30s
	stage_torch_prob = new float* [number];		// 这个是概率大小,维度：[数据有多少个30s][5(表示5个阶段的概率)], 5个阶段依次为: wake/n1/n2/n3/rem
	for (int i = 0; i < number; i++) {
		stage_torch_index[i] = stage[i].item<int>();
		//std::cout << stage_auto[i] << std::endl;
		stage_torch_prob[i] = new float[5];
		for (int j = 0; j < 5; j++) {
			stage_torch_prob[i][j] = output[i][j].item<float>();
		}
	}
	return;
}


/***
* @param Data: [通道, 整晚数据]
***/
/******* 调用部分 ********/
/**** 用到上面1个函数的部分程序 ******/
void stage_detection(float** Data){
	/*
#ifdef TEST
	qDebug() << "stage_delection...";
#endif
	// 不污染原始数据，将所需数据取出
	dlg->set_value_stage(0);
	QCoreApplication::processEvents();

	 edit
	get_time(0, 100);
	QTime t1 = QTime(hour, minite, second);    // 原来的开始时间
	get_time(num_stage * 3000, 100);
	QTime t2 = QTime(hour, minite, second);    // 原来的结束时间
	QTime t3 = ui->timeEdit_StartTime->time(); // 当前选择的开始时间
	QTime t4 = ui->timeEdit_EndTime->time();   // 当前选择的结束时间
	int k = distance_Of_Time(t1, t2);      // 表示总数据长度为多少秒
	int gap1 = distance_Of_Time(t3, t4);   // 当前所选时间段长度为多少秒
	int gap2 = distance_Of_Time(t1, t3);   // 开始时间相差多少秒
	int gap3 = distance_Of_Time(t4, t2);   // 结束时间相差多少秒
	*/
	int k = 1000 * 30;
	int flag_TimeChanged = 0;

	// 当前所选数据的总长度为多少秒
	int length_Now = 0;
	if (flag_TimeChanged == 0) {
		length_Now = k;
	}
	//else {
	//	length_Now = gap1;
	//}

	/*********** 分期用到的通道就是这三种 ***********/
	float** Data_EEG, ** Data_EOG, * Data_EMG;   // EEG脑电，EOG眼电，EMG肌电
	/**** 脑电和眼电都各自有两个通道,所以是二维数组  ******/
	Data_EEG = new float* [2];
	Data_EOG = new float* [2];

	/**** 肌电是一通道 ****/
	Data_EMG = new float[header.frequency[header.ID_electrode[4]] * length_Now];

	// 第1，2通道是脑电，第3，4通道是眼电
	if (flag_TimeChanged == 0)
	{
		for (int i = 0; i < 2; i++)
		{
			Data_EEG[i] = new float[header.frequency[header.ID_electrode[i]] * header.nrecords * header.duration];
			Data_EOG[i] = new float[header.frequency[header.ID_electrode[i + 2]] * header.nrecords * header.duration];
		}
	}
	//else	// 不知道flat_TimeChanged咋用
	//{
	//	for (int i = 0;i < 2;i++)
	//	{
	//		Data_EEG[i] = new float[header.frequency[header.ID_electrode[i]] * gap1];
	//		Data_EOG[i] = new float[header.frequency[header.ID_electrode[i + 2]] * gap1];
	//	}
	//}

	// 取数据
	if (flag_TimeChanged == 0)
	{
		for (int i = 0; i < 100 * length_Now; i++) {
			Data_EEG[0][i] = Data[header.ID_electrode[0]][i] - Data[header.ID_electrode[5]][i];
			Data_EEG[1][i] = Data[header.ID_electrode[1]][i] - Data[header.ID_electrode[6]][i];
		}
		for (int i = 0; i < 100 * length_Now; i++) {
			Data_EOG[0][i] = Data[header.ID_electrode[2]][i] - Data[header.ID_electrode[7]][i];
			Data_EOG[1][i] = Data[header.ID_electrode[3]][i] - Data[header.ID_electrode[8]][i];
		}
		for (int i = 0; i < 100 * length_Now; i++) {
			Data_EMG[i] = Data[header.ID_electrode[4]][i] - Data[header.ID_electrode[9]][i];;
		}

	}
	//else{}???

	// 取完数据不用计算均值(channel_mean)，先不用零相位滤波(filter_zero)
	int num = int(length_Now / 30);       // num是以30秒划分后的数据数量

	// resampled_data:输入模型的数据，对应原程序的时频矩阵(timespectrom)
	// shape = [n_epoch, 5, 3750(30*125Hz)]
	float*** resampled_data;
	resampled_data = new float** [num];
	for (int i = 0; i < num; i++) {
		resampled_data[i] = new float* [5];
		for (int j = 0; j < 5; j++) {
			resampled_data[i][j] = new float[3750];
		}
	}

	// int *flag_stage1, *flag_stage2, *flag_stage3;不知道是干嘛的
	float* EEG1_T, * EEG2_T, * EOG1_T, * EOG2_T, * EMG_T;  // 分别以原始数据频率来存储EEG, EOG, EMG数据
	EEG1_T = new float[header.frequency[header.ID_electrode[0]] * 30];
	EEG2_T = new float[header.frequency[header.ID_electrode[0]] * 30];
	EOG1_T = new float[header.frequency[header.ID_electrode[2]] * 30];
	EOG2_T = new float[header.frequency[header.ID_electrode[2]] * 30];
	EMG_T = new float[header.frequency[header.ID_electrode[4]] * 30];

	// “检查30s窗是否异常”(flagstage) 不知道是干嘛的
	for (int i = 0; i < num; i++)
	{
		// EEG窗
		for (int j = 0; j < header.frequency[header.ID_electrode[0]] * 30; j++)
		{
			EEG1_T[j] = Data_EEG[0][j + long(i * header.frequency[header.ID_electrode[0]] * 30)];
			EEG2_T[j] = Data_EEG[1][j + long(i * header.frequency[header.ID_electrode[0]] * 30)];
		}
		// EOG窗
		for (int j = 0; j < header.frequency[header.ID_electrode[2]] * 30; j++)
		{
			EOG1_T[j] = Data_EOG[0][j + long(i * header.frequency[header.ID_electrode[2]] * 30)];
			EOG2_T[j] = Data_EOG[1][j + long(i * header.frequency[header.ID_electrode[2]] * 30)];
		}
		// EMG窗
		for (int j = 0; j < header.frequency[header.ID_electrode[4]] * 30; j++)
			EMG_T[j] = Data_EMG[j + long(i * header.frequency[header.ID_electrode[4]] * 30)];

		// 重采样至125Hz
		resample(EEG1_T, resampled_data[i][0], header.frequency[header.ID_electrode[0]]);
		resample(EEG2_T, resampled_data[i][1], header.frequency[header.ID_electrode[0]]);
		resample(EOG1_T, resampled_data[i][2], header.frequency[header.ID_electrode[0]]);
		resample(EOG2_T, resampled_data[i][3], header.frequency[header.ID_electrode[0]]);
		resample(EMG_T, resampled_data[i][4], header.frequency[header.ID_electrode[0]]);
		// 不做STFT
	}

	// 重采样后直接输入模型
	// resampled_data shape: [n_epoch, 5, 3750]
	torch_process(resampled_data);
}


int main()
{
	//测试libtorch api
	torch::Tensor myTensor = torch::rand({ 5, 3 }) - 0.5;
	std::cout << myTensor << std::endl << std::endl;

	/* //测试torch_process()的
	float*** d3d;
	d3d = new float** [5];//5chn, 1000epoch, 3750sample
	for (int i = 0;i < 5;i++) {
		d3d[i] = new float*[1000];
		for (int j = 0;j < 1000; j++) {
			d3d[i][j] = new float[3750];
			for (int k =0;k< 3750;k++)
				d3d[i][j][k] = (float)rand() / 32767 - .5;
		}
	}
	torch_process(d3d);***/


	// 用来输入的随机数据 [10, 1000 * 30 * 100] (chn, n_epoch * 30s * 100hz)
	float** d2d;
	d2d = new float* [10];
	for (int i = 0; i < 10; i++) {
		d2d[i] = new float[(30 * 1000) * 100];
		for (int j = 0; j < (30 * 1000) * 100; j++) {
			d2d[i][j] = (float)rand() / 32767 - .5;
		}
	}

	// demo
	stage_detection(d2d);

	// disp result
	for (int i = 0; i < 1000; i++) {
		std::cout << stage_torch_index[i] << " ";
	}
	std::cout << std::endl;
	for (int i = 0; i < 5; i++) {
		std::cout << stage_torch_prob[123][i] << " ";
	}

	return EXIT_SUCCESS;
}