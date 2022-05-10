package com.example.testversion.service;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class bleDataViewModel extends AndroidViewModel {
    // Create a LiveData with a String
    private MutableLiveData<String> mCurrentName;
    // Create a LiveData with a String list
    private MutableLiveData<int[]> mArrayData;

    public bleDataViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> getCurrentName() {
        if (mCurrentName == null) {
            mCurrentName = new MutableLiveData<>();
        }
        return mCurrentName;
    }

    public MutableLiveData<int[]> getDataList(){
        if (mArrayData == null) {
            mArrayData = new MutableLiveData<>();
        }
        return mArrayData;
    }
}
