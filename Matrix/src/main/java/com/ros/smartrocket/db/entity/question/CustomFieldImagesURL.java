package com.ros.smartrocket.db.entity.question;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.entity.BaseEntity;

import java.io.Serializable;

public class CustomFieldImagesURL extends BaseEntity {
    @BaseEntity.SkipFieldInContentValues
    private static final long serialVersionUID = 4845715664252477541L;
    @SerializedName("Image_Url")
    public String imgUrl;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public CustomFieldImagesURL() {
    }


    public static CustomFieldImagesURL[] getCustomFieldImagesArray(String jsonArrayString) {
        CustomFieldImagesURL[] customFieldImagesURLS = new CustomFieldImagesURL[]{};
        if (!TextUtils.isEmpty(jsonArrayString)) {
            customFieldImagesURLS = new Gson().fromJson(jsonArrayString, CustomFieldImagesURL[].class);
        }
        return customFieldImagesURLS;
    }
}
