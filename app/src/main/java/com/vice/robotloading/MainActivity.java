package com.vice.robotloading;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.vice.robotloadinglib.RobotLoadingLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RobotLoadingLayout robotLoadingLayout = (RobotLoadingLayout) View.inflate(MainActivity.this, R.layout.robot_loading_layout, null);
                final Dialog dialog = new Dialog(MainActivity.this, R.style.FullScreenDialog);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(robotLoadingLayout);
                dialog.show();
                robotLoadingLayout.startLoading();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(8000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                robotLoadingLayout.close(new RobotLoadingLayout.CloseListener() {
                                    @Override
                                    public void close() {
                                        if (dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }).start();
            }
        });
    }
}
