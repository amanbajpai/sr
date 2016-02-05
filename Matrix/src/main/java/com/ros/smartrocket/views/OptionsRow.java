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
import com.ros.smartrocket.bl.WavesBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Locale;

import static com.ros.smartrocket.utils.UIUtils.getBalanceOrPrice;

public final class OptionsRow extends LinearLayout {
    private final Context context;

    @Bind(R.id.optionsRowPrice)
    TextView priceTextView;
    @Bind(R.id.optionsRowExp)
    TextView expTextView;
    @Bind(R.id.optionsRowLocations)
    TextView locationsTextView;

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
        priceTextView.setText(getBalanceOrPrice(task.getPrice(), task.getCurrencySign()));

        expTextView.setCompoundDrawablesWithIntrinsicBounds(expResId, 0, 0, 0);
        expTextView.setText(String.format(Locale.US, "%.0f", task.getExperienceOffer()));
    }

    public void setData(Wave wave, boolean isWaveDetails) {
        int colorResId;
        int priceResId;
        int locationResId;
        int expResId;

        if (WavesBL.isPreClaimWave(wave)) {
            colorResId = isWaveDetails ? R.color.violet_dark : R.color.violet;
            priceResId = R.drawable.wallet_violet;
            expResId = R.drawable.rocket_violet;
            locationResId = R.drawable.location_violet;
        } else {
            colorResId = isWaveDetails ? R.color.green_light : R.color.green;
            priceResId = R.drawable.wallet_green;
            expResId = R.drawable.rocket_green;
            locationResId = R.drawable.location_green;
        }

        setBackgroundColor(getResources().getColor(colorResId));

        if (isWaveDetails) {
            priceTextView.setText(UIUtils.getBalanceOrPrice(wave.getNearTaskPrice(), wave.getNearTaskCurrencySign()));
        } else {
            priceTextView.setText(getBalanceOrPrice(wave.getRate(), wave.getNearTaskCurrencySign()));
            if (wave.isContainsDifferentRate()) {
                priceTextView.append("+");
            }
        }
        priceTextView.setCompoundDrawablesWithIntrinsicBounds(priceResId, 0, 0, 0);

        expTextView.setText(String.format(Locale.US, "%.0f", wave.getExperienceOffer()));
        expTextView.setCompoundDrawablesWithIntrinsicBounds(expResId, 0, 0, 0);

        locationsTextView.setVisibility(VISIBLE);
        locationsTextView.setText(String.valueOf(wave.getTaskCount()));
        locationsTextView.setCompoundDrawablesWithIntrinsicBounds(locationResId, 0, 0, 0);
    }
}
