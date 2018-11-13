package com.ros.smartrocket.presentation.question.instruction;

import android.graphics.Bitmap;

import com.ros.smartrocket.presentation.question.base.BaseQuestionMvpView;

import java.io.File;

interface InstructionMvpView extends BaseQuestionMvpView {
    void setVideoInstructionFile(File file);

    void setImageInstruction(Bitmap bitmap, String filePath);
}
