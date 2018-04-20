package com.example.btf.recycleView;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btf.R;
import com.example.btf.Util.ActivityCollector;
import com.example.btf.Util.HttpAction;
import com.example.btf.Util.HttpUtil;
import com.example.btf.db.InfoDetail;
import com.example.btf.recycleView.indexBar.ButterflyInfo_copy;
import com.example.btf.recycleView.search.HeaderAdapter;
import com.example.btf.recycleView.search.RecyclerOnItemClickListener;
import com.example.btf.recycleView.search.SearchButterflyInfoAdapter;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ButterflyActivity extends AppCompatActivity implements RecyclerOnItemClickListener.OnItemClickListener {

    @BindView(R.id.toolbar_recycler_view)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.search_view)
    MaterialSearchView searchView;
    @BindView(R.id.swipsh_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    LinearLayoutManager layoutManager;

    private SearchButterflyInfoAdapter butterflyAdapter;
    private SuspensionDecoration butterflyDecoration;
    /**
     * 右侧边栏导航区域
     */
    private IndexBar mIndexBar;

    /**
     * 显示指示器DialogText
     */
    private TextView mTvSideBarHint;

    /**
     * 顶部首字母
     */
    private HeaderAdapter adapter;

    /**
     * 显示进度条
     */
    private ProgressDialog progressDialog;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private IntentFilter intentFilter;
    private NetworkChangeReciver networkChangeReciver;

    String address = "http://40.125.207.182:8080/getInfo.do";
    List<InfoDetail> butterflyInfoList = new ArrayList<>();

    List<ButterflyInfo_copy> butterflyInfo_copyList = new ArrayList<>();

    private static boolean readDBFlag = false;
    private static boolean dataFlag = true;
    private static boolean netFlag=true;
    public static String[] images = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycle_view);
        ActivityCollector.AddActivity(this);
        ButterKnife.bind(this);
        //初始化资源文件
        sp = getSharedPreferences("com_example_butterfly_recognition_data", MODE_PRIVATE);
        editor = getSharedPreferences("com_example_butterfly_recognition_data", MODE_PRIVATE).edit();
//        readDBFlag = sp.getBoolean("DB_EXIST", false);
        dataFlag = sp.getBoolean("DATA_EXIST", false);
//        if (!readDBFlag) {
//            readDBFlag = SQLUtil.createDatabase(getApplicationContext());
//            editor.putBoolean("DB_EXIST", readDBFlag);
//        }
        if (!dataFlag) {
            String jsonData = readAssetsTxt(getApplicationContext(), "data");
            Log.i("data", jsonData);
            if (jsonData != null) {
                dataFlag=true;
            }
            editor.putString("old_data", jsonData);
            editor.putBoolean("DATA_EXIST", dataFlag);
        }
        editor.apply();
        LitePal.initialize(this);
        initView();

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReciver = new NetworkChangeReciver();
        registerReceiver(networkChangeReciver, intentFilter);
    }

    private void initView() {
        initToolBar();
        initRecyclerView();
        initSearchView();
        initSwipeRefreshLayout();
    }

    /**
     * init Toolbar
     */
    private void initToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


    private void initButterfly() {
        LitePal.initialize(getApplicationContext());
        List<InfoDetail> list = DataSupport.findAll(InfoDetail.class);
        butterflyInfoList.clear();
        butterflyInfo_copyList.clear();
        ButterflyInfo_copy butterflyInfo_copy;
        for (InfoDetail butterflyInfo : list) {
            butterflyInfo_copy = new ButterflyInfo_copy();
            butterflyInfo_copy.setId(butterflyInfo.getId());
            butterflyInfo_copy.setName(butterflyInfo.getName());
            butterflyInfo_copy.setLatinName(butterflyInfo.getLatinName());
            butterflyInfo_copy.setFeature(butterflyInfo.getFeature());
            butterflyInfo_copy.setType(butterflyInfo.getType());
            butterflyInfo_copy.setRare(butterflyInfo.getRare());
            butterflyInfo_copy.setProtect(butterflyInfo.getProtect());
            butterflyInfo_copy.setImagePath(butterflyInfo.getImagePath());
            butterflyInfo_copy.setImageUrl(butterflyInfo.getImageUrl());
            butterflyInfo_copy.setUniqueToChina(butterflyInfo.getUniqueToChina());
            butterflyInfoList.add(butterflyInfo);
            butterflyInfo_copyList.add(butterflyInfo_copy);
        }
    }

    /**
     * init RecyclerView
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void initRecyclerView() {
        initButterfly();

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        butterflyAdapter = new SearchButterflyInfoAdapter(butterflyInfo_copyList);
        recyclerView.setAdapter(butterflyAdapter);
        adapter = new HeaderAdapter(butterflyAdapter);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(butterflyDecoration = new SuspensionDecoration(this, butterflyInfo_copyList));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//LinearLayoutManager.VERTICAL

        //使用indexBar
        mTvSideBarHint = (TextView) findViewById(R.id.tvSideBarHint);//HintTextView
        mIndexBar = (IndexBar) findViewById(R.id.indexBar);//IndexBar

        //indexbar初始化
        mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setmLayoutManager(layoutManager);//设置RecyclerView的LayoutManager
        mIndexBar.setmSourceDatas(butterflyInfo_copyList)//设置数据
                .invalidate();
        butterflyAdapter.setDatas(butterflyInfo_copyList);
        if (butterflyInfo_copyList.size() < 5) {
            mIndexBar.setVisibility(View.INVISIBLE);//若列表数目小于5则不显示索引栏
        }
        butterflyDecoration.setmDatas(butterflyInfo_copyList);
    }

    /**
     * init SearchView
     */
    private void initSearchView() {
        searchView.setHint("请输入蝴蝶的中文名或拉丁名");
        searchView.setVoiceSearch(false);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setEllipsize(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<ButterflyInfo_copy> filteredModelList = filter(butterflyInfo_copyList, newText);
                //reset
                butterflyAdapter.setFilter(filteredModelList);
                butterflyAdapter.animateTo(filteredModelList);
                recyclerView.scrollToPosition(0);
                return true;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {
                butterflyAdapter.setFilter(butterflyInfo_copyList);
            }
        });
    }

    /**
     * init IndexBar
     */
    private void initIndexBar() {
        mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setmLayoutManager(layoutManager);//设置RecyclerView的LayoutManager
        mIndexBar.setmSourceDatas(butterflyInfo_copyList)//设置数据
                .invalidate();
        butterflyAdapter.setDatas(butterflyInfo_copyList);
        butterflyDecoration.setmDatas(butterflyInfo_copyList);
    }

    /**
     * init SwiperRefreshLayout
     */
    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            swipeRefreshLayout.setElevation((float) 0.0);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isAvailable()) {
                    refresh();
                } else {
                    Toast.makeText(ButterflyActivity.this, "无法刷新！", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    /**
     * 读取assets下的txt文件，返回utf-8 String
     * @param context
     * @param fileName 不包括后缀
     * @return
     */
    public static String readAssetsTxt(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName + ".txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
//            String text = new String(buffer, "utf-8");
            String text = new String(buffer).trim();
            return text;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "读取错误，请检查文件名";
    }

    private void refresh() {
        if (netFlag) {
            HttpUtil.sendOkHttpRequest(address, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responeData = response.body().string();
                    sp = getSharedPreferences("com_example_butterfly_recognition_data", MODE_PRIVATE);
                    String oldData = sp.getString("old_data", null).trim();
                    if (!responeData.equals(oldData)) {
                        String handlerespone = handleResponse(oldData, responeData);
                        boolean result = false;
                        try {
                            result = HttpAction.parseJSONWithGSON(handlerespone);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (result) {
//                            Log.e("result-true", String.valueOf(result));
                            ButterflyActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    editor = getSharedPreferences("com_example_butterfly_recognition_data", MODE_PRIVATE).edit();
                                    editor.remove("old_data");
                                    editor.putString("old_data", responeData);
                                    editor.apply();
                                    initButterfly();
                                    butterflyAdapter.notifyDataSetChanged();
                                    initIndexBar();
                                    swipeRefreshLayout.setRefreshing(false);
                                    Toast.makeText(ButterflyActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            ButterflyActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefreshLayout.setRefreshing(false);
                                    Toast.makeText(ButterflyActivity.this, "无更新！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        ButterflyActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(ButterflyActivity.this, "没有新数据！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    ButterflyActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(ButterflyActivity.this, "Fail!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            Toast.makeText(ButterflyActivity.this, "网络无连接!", Toast.LENGTH_SHORT).show();
        }
    }

    private String handleResponse(String oldData, String newData) {
        String head = newData.substring(0, 35);
        String oldInfo = oldData.substring(35, oldData.length() - 2);
        String newInfo = newData.substring(35, newData.length() - 2);
        String[] oldlist = oldInfo.split("(?<=\\}),(?=\\{)");
        String[] newlist = newInfo.split("(?<=\\}),(?=\\{)");
        String result = "";
        String finalResult = "";
        if (oldlist.length == newlist.length) {
//            Log.e("=", "");
            for (int i = 0; i < newlist.length; i++) {
                if (oldlist[i].equals(newlist[i])) {
                    continue;
                }
//                Log.e("olds", oldlist[i] + "\n");
//                Log.e("news", newlist[i]+"\n");
                result = result + "," + newlist[i];
            }
            finalResult = head + result.substring(1, result.length()) + "]}" + "^=";
        } else if (oldlist.length < newlist.length) {
//            Log.e("<", "");
            for (int i = oldlist.length; i < newlist.length; i++) {
                result = result + "," + newlist[i];
            }
            finalResult = head + result.substring(1, result.length()) + "]}" + "^+";
        } else {
//            Log.e(">", "");
            for (int i = newlist.length; i < oldlist.length; i++) {
                result = result + "," + oldlist[i];
            }
            finalResult = head + result.substring(1, result.length()) + "]}" + "^-";
        }
//        Log.e("final_result", finalResult);
        return finalResult;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReciver);
    }

    /**
     * 筛选逻辑
     *
     * @param butterflyinfos
     * @param query
     * @return
     */
    private List<ButterflyInfo_copy> filter(List<ButterflyInfo_copy> butterflyinfos, String query) {

        final List<ButterflyInfo_copy> filteredModelList = new ArrayList<>();
        for (ButterflyInfo_copy butterflyinfo : butterflyinfos) {
            final String name = butterflyinfo.getName();
            final String latinName = butterflyinfo.getLatinName();
            if (name.contains(query) || latinName.contains(query)||latinName.toLowerCase().contains(query)) {
                filteredModelList.add(butterflyinfo);
            }
        }
        return filteredModelList;
    }

    /**
     * 搜索按钮
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 返回按钮处理
     */
    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();

        } else {
            super.onBackPressed();
            finish();
            //实现淡入淡出的切换效果
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    /**
     * 筛选传递
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class NetworkChangeReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                netFlag=true;
            } else {
                netFlag=false;
                Toast.makeText(context, "无法连接至服务器，请稍后再试", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        InfoDetail butterflyInfo = butterflyInfoList.get(position);
        Intent intent = new Intent(this, InfoActivity.class);
        intent.putExtra("butterflyName", butterflyInfo.getName());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}