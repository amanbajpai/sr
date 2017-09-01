// Generated code from Butter Knife. Do not modify!
package com.ros.smartrocket.flow.login;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v4.view.ViewPager;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.rd.PageIndicatorView;
import com.ros.smartrocket.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class TutorialActivity_ViewBinding implements Unbinder {
  private TutorialActivity target;

  @UiThread
  public TutorialActivity_ViewBinding(TutorialActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public TutorialActivity_ViewBinding(TutorialActivity target, View source) {
    this.target = target;

    target.viewPager = Utils.findRequiredViewAsType(source, R.id.viewPager, "field 'viewPager'", ViewPager.class);
    target.pageIndicatorView = Utils.findRequiredViewAsType(source, R.id.pageIndicatorView, "field 'pageIndicatorView'", PageIndicatorView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    TutorialActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.viewPager = null;
    target.pageIndicatorView = null;
  }
}
