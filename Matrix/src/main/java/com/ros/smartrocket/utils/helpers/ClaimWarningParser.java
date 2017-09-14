package com.ros.smartrocket.utils.helpers;

import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Warning;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.utils.UIUtils;

public class ClaimWarningParser {

    public void parseWarnings(Warning[] warnings) {
        if (warnings != null && warnings.length > 0) {
            Warning warning = warnings[0];
            if (warning.getCode() == NetworkError.HALF_CLAIM_PER_MISSION_CODE
                    && warning.getParams() != null
                    && warning.getParams().length > 1) {

                int currentClaim = warning.getParams()[0];
                int maxClaim = warning.getParams()[1];

                String message;
                if (currentClaim == maxClaim) {
                    message = App.getInstance().getString(R.string.warning_last_claim, maxClaim);
                } else {
                    message = App.getInstance().getString(R.string.warning_half_claims,
                            currentClaim, maxClaim);
                }
                UIUtils.showToastCustomDuration(message, 8000);
            }
        }
    }
}
