package com.ros.smartrocket.presentation.question.audit.additional;

import com.ros.smartrocket.db.entity.Question;

public interface Navigator {

    void openThumbnailDialog(String path);

    void startSubQuestionsFragment(CategoryProductPair item, Question question, boolean isRedo, boolean isPreview);
}
