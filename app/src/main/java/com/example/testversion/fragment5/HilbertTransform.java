// Catalano Math Library
// The Catalano Framework
//
// Copyright © Diego Catalano, 2012-2016
// diego.catalano at live.com
//
// Copyright © César Souza, 2009-2014
// cesarsouza at gmail.com
//
//    This library is free software; you can redistribute it and/or
//    modify it under the terms of the GNU Lesser General Public
//    License as published by the Free Software Foundation; either
//    version 2.1 of the License, or (at your option) any later version.
//
//    This library is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//    Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public
//    License along with this library; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//

package com.example.testversion.fragment5;
import org.apache.commons.math3.complex.Complex;

/**
 * Fast Hilbert Transform.
 * @author Diego Catalano
 */
public class HilbertTransform {
    
    /**
     * 1-D Fast Hilbert Transform.
     * @param data Data.
     * @param direction Direction.
     */
    public static double[] FHT(double[] data, FourierTransform.Direction direction){
        
        int N = data.length;

        // 如果进行傅里叶正变换
        if (direction == FourierTransform.Direction.Forward){
            
            // 将输入复制到一个可以处理的复数组中
            //  在复数域进行FFT处理
            Complex[] cdata = new Complex[N];
            for (int i = 0; i < N; i++) {
                cdata[i] = new Complex(data[i], 0);
            }
            // 执行 FFT
            FourierTransform.FFT(cdata, FourierTransform.Direction.Forward);

            //双正频
            for (int i = 1; i < (N/2); i++)
            {
//                    cdata[i].real *= 2.0;
//                    cdata[i].imaginary *= 2.0;
                cdata[i] = Complex.valueOf(cdata[i].getReal()*2.0,cdata[i].getImaginary()*2.0);
            }

            // 将负数频率清零
            //  (去除直流分量）
            for (int i = (N/2)+1; i < N; i++)
            {
//                    cdata[i].real = 0.0;
//                    cdata[i].imaginary = 0.0;
                cdata[i] = Complex.valueOf(0.0,0.0);
            }

            // 傅里叶逆变换
            FourierTransform.FFT(cdata, FourierTransform.Direction.Backward);

            // 转换回我们最初的双数组
            for (int i = 0; i < N; i++) {
                data[i] = Math.hypot(cdata[i].getReal(), cdata[i].getImaginary());
            }
            }

        else { // 傅里叶逆变换
            // 逆希尔伯特变换可以通过逆变换和重新应用变换来计算
            // H^–1{h(t)} = –H{h(t)}

            FHT(data, FourierTransform.Direction.Forward);

            for (int i = 0; i < data.length; i++) {
                data[i] = -data[i];
            }
        }
        return data;
    }
    
    /**
     * 1-D Fast Hilbert Transform.
     * @param data Data.
     * @param direction Direction.
     */
    public static void FHT(Complex[] data, FourierTransform.Direction direction){
        int N = data.length;

        // Forward operation
        if (direction == FourierTransform.Direction.Forward){
            // Makes a copy of the data so we don't lose the
            //  original information to build our final signal
            Complex[] shift = (Complex[])data.clone();

            // Perform FFT
            FourierTransform.FFT(shift, FourierTransform.Direction.Backward);

            //double positive frequencies
              for (int i = 1; i < (N/2); i++){
//                    shift[i].real *= 2.0;
//                    shift[i].imaginary *= 2.0;
                    shift[i] = Complex.valueOf(shift[i].getReal()*2.0,shift[i].getImaginary()*2.0);

            }

            // zero out negative frequencies
            //  (leaving out the dc component)
            for (int i = (N/2)+1; i < N; i++){
//                    shift[i].real = 0.0;
//                    shift[i].imaginary = 0.0;
                shift[i] = Complex.valueOf(0.0,0.0);
            }

            // Reverse the FFT
            FourierTransform.FFT(shift, FourierTransform.Direction.Forward);

            // Put the Hilbert transform in the Imaginary part
            //  of the input signal, creating a Analytic Signal
            for (int i = 0; i < N; i++) {
//                    data[i].imaginary = shift[i].getImaginary();
                data[i] = Complex.valueOf(data[i].getReal(), shift[i].getImaginary());
            }
        }
        // Backward operation
        else{
            // Just discard the imaginary part
            for (int i = 0; i < data.length; i++) {
                data[i] = Complex.valueOf(data[i].getReal(), 0.0);
            }
        }
    }
    
    /**
     * 2-D Fast Hilbert Transform.
     * @param data Data.
     * @param direction Direction.
     */
    public static void FHT2(Complex[][] data, FourierTransform.Direction direction){
        
        int n = data.length;
        int m = data[0].length;
        Complex[] row = new Complex[Math.max(m, n)];
        
        for ( int i = 0; i < n; i++ ){
                // copy row
                for ( int j = 0; j < n; j++ )
                        row[j] = data[i][j];
                // transform it
                FHT( row, direction );
                // copy back
                for ( int j = 0; j < n; j++ ) {
                    data[i][j] = row[j];
                }
        }

        // process columns
        Complex[]	col = new Complex[n];

        for ( int j = 0; j < n; j++ ){
                // copy column
                for ( int i = 0; i < n; i++ ) {
                    col[i] = data[i][j];
                }
                // transform it
                FHT( col, direction );
                // copy back
                for ( int i = 0; i < n; i++ ) {
                    data[i][j] = col[i];
                }
        }
    }
}