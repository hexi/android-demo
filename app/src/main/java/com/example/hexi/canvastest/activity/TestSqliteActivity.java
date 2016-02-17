package com.example.hexi.canvastest.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.hexi.canvastest.R;
import com.example.hexi.canvastest.db.DBHelper;

/**
 * Created by hexi on 15/11/29.
 */
public class TestSqliteActivity extends Activity {

    private static final String TAG = "TestSqliteActivity";
    int size = 100000;
    String log = "this is a log, good night for you to find a persion";
    final String[] logDatas = new String[size];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite_test);

        for (int i = 0; i < size; i++) {
            logDatas[i] = log + i;
        }

    }

    public void save_no_transaction_then_get(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper.getInstance(getApplicationContext()).save(logDatas);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DBHelper.getInstance(getApplicationContext()).countLog();
            }
        }).start();
    }

    public void save_with_transaction_then_get(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper.getInstance(getApplicationContext())
                        .saveWithTransaction(logDatas);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DBHelper.getInstance(getApplicationContext()).countLog();
            }
        }).start();
    }

    public void save_with_transaction(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper.getInstance(getApplicationContext())
                        .saveWithTransaction(logDatas);
            }
        }).start();
    }

    public void get_get(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper.getInstance(getApplicationContext())
                        .getLastLogDataIdSleep();
            }
        }, "thread0").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DBHelper.getInstance(getApplicationContext()).countLog();
            }
        }, "thread1").start();
    }

    public void get(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DBHelper.getInstance(getApplicationContext()).countLog();
            }
        }, "thread1").start();
    }

    public void get_no_transaction_then__save(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int id = DBHelper.getInstance(getApplicationContext())
                        .getLastLogDataIdSleep();
                Log.d(TAG, "===id:" + id);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DBHelper.getInstance(getApplicationContext()).save(logDatas);

            }
        }).start();
    }

    public void get_then_save_with_transaction(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int id = DBHelper.getInstance(getApplicationContext())
                        .countLog();
                Log.d(TAG, "===id:" + id);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DBHelper.getInstance(getApplicationContext())
                        .saveWithTransaction(logDatas);

            }
        }).start();
    }

    public void save_then_save_with_transaction(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper.getInstance(getApplicationContext()).save(logDatas);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DBHelper.getInstance(getApplicationContext()).saveWithTransaction(logDatas);

            }
        }).start();
    }

    public void save_with_transaction_then_save(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper.getInstance(getApplicationContext()).saveWithTransaction(logDatas);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                DBHelper.getInstance(getApplicationContext()).save(logDatas);
            }
        }).start();
    }

    public void count_user(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper.getInstance(getApplicationContext()).countUser();
            }
        }).start();
    }

    public void save_no_transaction_then_count_user(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper.getInstance(getApplicationContext()).save(logDatas);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DBHelper.getInstance(getApplicationContext()).countUser();
            }
        }).start();
    }

    public void save_transaction_then_count_user(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper.getInstance(getApplicationContext()).saveWithTransaction(logDatas);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DBHelper.getInstance(getApplicationContext()).countUser();
            }
        }).start();
    }
}
