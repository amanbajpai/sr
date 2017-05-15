package com.ros.smartrocket.views;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.bl.WavesBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.ros.smartrocket.utils.UIUtils.getBalanceOrPrice;

public final class OptionsRow extends LinearLayout {
    private final static int LEFT_RIGHT_PADDING = 15;

    private final Context context;

    @Bind(R.id.optionsRowPrice)
    TextView priceTextView;
    @Bind(R.id.optionsRowExp)
    TextView expTextView;
    @Bind(R.id.optionsRowLocations)
    TextView locationsTextView;
    @Bind(R.id.optionsRowDuration)
    TextView durationTextView;

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

        int leftRightDp = UIUtils.getPxFromDp(context, LEFT_RIGHT_PADDING);
        setPadding(leftRightDp, 0, leftRightDp, 0);
    }

    public void setData(Task task) {
        int bgColorResId;
        int iconColorResId;
        int priceResId;
        int expResId;

        switch (TasksBL.getTaskStatusType(task.getStatusId())) {
            case NONE:
            case CLAIMED:
            case STARTED:
                if (TasksBL.isPreClaimTask(task)) {
                    bgColorResId = R.color.violet;
                    iconColorResId = R.color.icon_violet;
                    priceResId = R.drawable.wallet_violet;
                    expResId = R.drawable.rocket_violet;
                } else {
                    bgColorResId = R.color.green;
                    iconColorResId = R.color.icon_green;
                    priceResId = R.drawable.wallet_green;
                    expResId = R.drawable.rocket_green;
                }
                break;
            case SCHEDULED:
            case PENDING:
                bgColorResId = R.color.blue;
                iconColorResId = R.color.icon_blue;
                priceResId = R.drawable.wallet_blue;
                expResId = R.drawable.rocket_blue;
                break;
            case COMPLETED:
                bgColorResId = R.color.grey;
                iconColorResId = R.color.icon_grey;
                priceResId = R.drawable.wallet_grey;
                expResId = R.drawable.rocket_grey;
                break;
            case VALIDATION:
                bgColorResId = R.color.grey;
                iconColorResId = R.color.icon_grey_light;
                priceResId = R.drawable.wallet_lightgrey;
                expResId = R.drawable.rocket_lightgrey;
                break;
            case RE_DO_TASK:
                bgColorResId = R.color.red_dark;
                iconColorResId = R.color.icon_red;
                priceResId = R.drawable.wallet_red;
                expResId = R.drawable.rocket_red;
                break;
            case VALIDATED:
            case IN_PAYMENT_PROCESS:
            case PAID:
                bgColorResId = R.color.orange;
                iconColorResId = R.color.icon_gold;
                priceResId = R.drawable.wallet_gold;
                expResId = R.drawable.rocket_gold;
                break;
            case REJECTED:
                bgColorResId = R.color.black_light;
                iconColorResId = R.color.icon_grey;
                priceResId = R.drawable.wallet_grey;
                expResId = R.drawable.rocket_grey;
                break;
            default:
                bgColorResId = R.color.green;
                iconColorResId = R.color.icon_green;
                priceResId = R.drawable.wallet_green;
                expResId = R.drawable.rocket_green;
                break;
        }

        setBackgroundColor(getResources().getColor(bgColorResId));
        LocaleUtils.setCompoundDrawable(priceTextView, priceResId);
        priceTextView.setText(getBalanceOrPrice(task.getPrice(), task.getCurrencySign()));

        LocaleUtils.setCompoundDrawable(expTextView, expResId);
        expTextView.setText(String.format(Locale.US, "%.0f", task.getExperienceOffer()));

        Drawable drawable = getResources().getDrawable(R.drawable.stopwatch_timer_icon);
        if (drawable != null) {
            drawable.mutate().setColorFilter(getResources().getColor(iconColorResId), PorterDuff.Mode.MULTIPLY);
            if (LocaleUtils.isRtL()) {
                durationTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            } else {
                durationTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }
        }
        final String d = UIUtils.numberFormat.format(task.getApproxMissionDuration()) + getContext().getString(R.string.approx_mission_duration);
        durationTextView.setText(d);
    }

    public void setData(Wave wave, boolean isWaveDetails) {
        int bgColorResId;
        int priceResId;
        int expResId;
        int locationResId;
        int iconColorResId;

        if (WavesBL.isPreClaimWave(wave)) {
            bgColorResId = isWaveDetails ? R.color.violet_dark : R.color.violet;
            priceResId = R.drawable.wallet_violet;
            expResId = R.drawable.rocket_violet;
            locationResId = R.drawable.location_violet;
            iconColorResId = R.color.icon_violet;
        } else {
            bgColorResId = isWaveDetails ? R.color.green_light : R.color.green;
            priceResId = R.drawable.wallet_green;
            expResId = R.drawable.rocket_green;
            locationResId = R.drawable.location_green;
            iconColorResId = R.color.icon_green;
        }

        setBackgroundColor(getResources().getColor(bgColorResId));


        priceTextView.setText(getBalanceOrPrice(wave.getRate(), wave.getNearTaskCurrencySign()));
        if (wave.isContainsDifferentRate()) {
            priceTextView.append("+");
        }
        LocaleUtils.setCompoundDrawable(priceTextView, priceResId);

        expTextView.setText(String.format(Locale.US, "%.0f", wave.getExperienceOffer()));
        LocaleUtils.setCompoundDrawable(expTextView, expResId);

        locationsTextView.setVisibility(VISIBLE);
        locationsTextView.setText(String.valueOf(wave.getTaskCount()));
        LocaleUtils.setCompoundDrawable(locationsTextView, locationResId);

        Drawable drawable = getResources().getDrawable(R.drawable.stopwatch_timer_icon);
        if (drawable != null) {
            drawable.setColorFilter(getResources().getColor(iconColorResId), PorterDuff.Mode.MULTIPLY);
            if (LocaleUtils.isRtL()) {
                durationTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            } else {
                durationTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }
        }

        final String d = UIUtils.numberFormat.format(wave.getApproxMissionDuration())
                + getContext().getString(R.string.approx_mission_duration);
        durationTextView.setText(d);
    }
}
