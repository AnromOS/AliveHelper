package org.ancode.alivelib.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.ancode.alivelib.R;
import org.ancode.alivelib.config.HelperConfig;
import org.ancode.alivelib.utils.AliveLog;
import org.ancode.alivelib.utils.UiHelper;
import org.ancode.alivelib.utils.Utils;

/**
 * Created by andyliu on 16-8-24.
 */
public abstract class BaseAliveActivity extends Activity {
    private static final String TAG = BaseAliveActivity.class.getSimpleName();
    private View empty_view;
    private View error_view;
    private View loading_view;
    private View ssl_error;
    private View closeBtn;
    private View reloadBtn;
    private View topView;
    private TextView titleView;
    private TextView tvIsDeveloper;
    protected String data = "";
    private int themeColor = -1;
    private boolean applyStatusColor;
    private boolean InDevelopment = false;
    protected String appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        initBaseData();
        initBaseView();
        initView();
        if (!InDevelopment)
            loadData();
        AliveLog.v(TAG, "BaseAliveActivity onCreate");
    }

    protected void initBaseData() {
        themeColor = HelperConfig.THEME_COLOR_ID;
        appName = Utils.getAppName();
        applyStatusColor = getIntent().getBooleanExtra(HelperConfig.APPLY_STATUS_COLOR, true);
        InDevelopment = getIntent().getBooleanExtra(HelperConfig.IN_DEVELOPMENT, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public abstract void loadData();


    protected abstract int setLayout();

    protected abstract void initView();

    protected abstract void onRefresh(String data);

    protected abstract void reload();

    protected void setTitle(String title) {
        titleView.setText(title);
    }

    public void onReLoad() {
        showErrorView(false);
        showEmptyView(false);
        showLoading(true);
        loadData();
    }

    protected void initBaseView() {
        empty_view = findViewById(R.id.base_alive_empty_view);
        error_view = findViewById(R.id.base_alive_error_view);
        loading_view = findViewById(R.id.base_alive_loading_view);
        ssl_error = findViewById(R.id.https_ssl_error_view);
        topView = findViewById(R.id.top);
        titleView = (TextView) findViewById(R.id.title);
        closeBtn = findViewById(R.id.close);
        reloadBtn = findViewById(R.id.reload);
        tvIsDeveloper = (TextView) findViewById(R.id.tv_is_developer);
        setTimeColor();

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();
            }
        });
        empty_view.findViewById(R.id.empty_btnReload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onReLoad();
            }
        });

        error_view.findViewById(R.id.error_btnReload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onReLoad();
            }
        });
        if (!InDevelopment) {
            showLoading(true);
            tvIsDeveloper.setVisibility(View.GONE);
        } else {
            tvIsDeveloper.setVisibility(View.VISIBLE);
        }
    }


    public void showLoading(boolean show) {
        if (show) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loading_view.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loading_view.setVisibility(View.GONE);
                }
            });
        }
    }

    public void showErrorView(boolean show) {
        if (show) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    error_view.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    error_view.setVisibility(View.GONE);
                }
            });
        }
    }


    public void showSSLError(boolean show) {
        if (show) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ssl_error.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ssl_error.setVisibility(View.GONE);
                }
            });
        }
    }

    public void showEmptyView(boolean show) {
        if (show) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    empty_view.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    empty_view.setVisibility(View.GONE);
                }
            });
        }
    }

    private void setTimeColor() {
        int color = -1;
//        if (themeColor == -1) {
//            themeColor = Utils.getThemeColor();
//        }
        if (themeColor != -1) {

            color = getResources().getColor(themeColor);
        } else {
            color = UiHelper.getThemeColor();
        }

        topView.setBackgroundColor(color);
        if (applyStatusColor) {
            UiHelper.setStatusColor(this, color);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
