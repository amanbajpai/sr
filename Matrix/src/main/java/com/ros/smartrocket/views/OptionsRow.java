package com.ros.smartrocket.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Locale;

public final class OptionsRow extends LinearLayout {
    private final Context context;

    @Bind(R.id.optionsPrice)
    TextView priceTextView;
    @Bind(R.id.optionsExp)
    TextView expTextView;

    public OptionsRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.options_row_layout, this, true);
        ButterKnife.bind(this);

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    public void setData(Task task) {
        int colorResId;
        int priceResId;
        int expResId;

        switch (TasksBL.getTaskStatusType(task.getStatusId())) {
            case NONE:
            case CLAIMED:
            case STARTED:
                if (TasksBL.isPreClaimTask(task)) {
                    colorResId = R.color.violet;
                    priceResId = R.drawable.wallet_violet;
                    expResId = R.drawable.rocket_violet;
                } else {
                    colorResId = R.color.green;
                    priceResId = R.drawable.wallet_green;
                    expResId = R.drawable.rocket_green;
                }
                break;
            case SCHEDULED:
            case PENDING:
                colorResId = R.color.blue;
                priceResId = R.drawable.wallet_blue;
                expResId = R.drawable.rocket_blue;
                break;
            case COMPLETED:
                colorResId = R.color.grey;
                priceResId = R.drawable.wallet_grey;
                expResId = R.drawable.rocket_grey;
                break;
            case VALIDATION:
                colorResId = R.color.grey;
                priceResId = R.drawable.wallet_lightgrey;
                expResId = R.drawable.rocket_lightgrey;
                break;
            case RE_DO_TASK:
                colorResId = R.color.red_dark;
                priceResId = R.drawable.wallet_red;
                expResId = R.drawable.rocket_red;
                break;
            case VALIDATED:
            case IN_PAYMENT_PROCESS:
            case PAID:
                colorResId = R.color.orange;
                priceResId = R.drawable.wallet_gold;
                expResId = R.drawable.rocket_gold;
                break;
            case REJECTED:
                colorResId = R.color.black_light;
                priceResId = R.drawable.wallet_grey;
                expResId = R.drawable.rocket_grey;
                break;
            default:
                colorResId = R.color.green;
                priceResId = R.drawable.wallet_green;
                expResId = R.drawable.rocket_green;
                break;
        }

        setBackgroundColor(getResources().getColor(colorResId));

        priceTextView.setCompoundDrawablesWithIntrinsicBounds(priceResId, 0, 0, 0);
        priceTextView.setText(UIUtils.getBalanceOrPrice(context, task.getPrice(), task.getCurrencySign(), null, null));

        expTextView.setCompoundDrawablesWithIntrinsicBounds(expResId, 0, 0, 0);
        expTextView.setText(String.format(Locale.US, "%.0f", task.getExperienceOffer()));
    }
}
