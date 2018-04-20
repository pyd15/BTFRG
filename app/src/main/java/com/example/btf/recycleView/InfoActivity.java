package com.example.btf.recycleView;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.btf.R;
import com.example.btf.Util.ActivityCollector;
import com.example.btf.db.InfoDetail;
import com.example.btf.fragment.InfoFragment;
import com.example.btf.viewpage.GuardViewPager;
import com.example.btf.viewpage.VPAdapter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dr.P on 2017/10/10.
 */

public class InfoActivity extends AppCompatActivity {

    ImageView butterflypicture;
    TextView nametext;
    TextView latinNametext;
    TextView typetext;
    TextView featuretext;;
    TextView areatext;
    TextView protecttext;
    TextView raretext;
    TextView uniqueToChinatext;

    private GuardViewPager vp;
    private VPAdapter vpAdapter;
    private LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_layout);
        ActivityCollector.AddActivity(this);
        int id = getIntent().getIntExtra("butterflyNo", 1);
        List<InfoDetail> nameList = DataSupport.where("b_id=?",String.valueOf(id)).find(InfoDetail.class);
        ArrayList<String> imageList = getIntent().getStringArrayListExtra("imageList");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_info);
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.image_collapsing_toolbar);
        FloatingActionButton fab_fruit_content = (FloatingActionButton) findViewById(R.id.fab_fruit_content);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        InfoFragment infoFragment = (InfoFragment) getSupportFragmentManager().findFragmentById(R.id.info_fragment);
        infoFragment.refresh(getApplicationContext(),nameList,imageList);
//        vp = (GuardViewPager) findViewById(R.id.vp);
//        vpAdapter = new VPAdapter(this, imageList, nameList.get(0));
//        vp.setAdapter(vpAdapter);
//        ll = (LinearLayout) findViewById(R.id.ll);
//
//        nametext = (TextView) findViewById(R.id.name);
//        latinNametext = (TextView) findViewById(R.id.latinName);
//        typetext = (TextView) findViewById(R.id.type);
//        featuretext = (TextView) findViewById(R.id.feature);
//        areatext = (TextView) findViewById(R.id.area);
//        protecttext = (TextView) findViewById(R.id.protect);
//        raretext = (TextView) findViewById(R.id.rare);
//        uniqueToChinatext = (TextView) findViewById(R.id.uniqueToChina);
//
//        if (nameList.get(0).getName()!=null)
//        nametext.setText("中文学名:" + nameList.get(0).getName());
//        else nametext.setText("中文学名:暂无" );
//        if (nameList.get(0).getLatinName() != null)
//        latinNametext.setText("拉丁学名:" + nameList.get(0).getLatinName());
//        else latinNametext.setText("拉丁学名:暂无");
//        if(nameList.get(0).getType()!=null)
//        typetext.setText("科属:" + nameList.get(0).getType());
//        else typetext.setText("科属:暂无");
//        if(nameList.get(0).getFeature()!=null)
//        featuretext.setText("识别特征:\n" + nameList.get(0).getFeature());
//        else featuretext.setText("识别特征:\n暂无");
//        if (nameList.get(0).getArea()!=null)
//        areatext.setText("地理分布:\n" + nameList.get(0).getArea());
//        else areatext.setText("地理分布:\n暂无");
//        if (nameList.get(0).getProtect() == 0) {
//            protecttext.setText("保护级别:非保护品种");
//        } else {
//            protecttext.setText("保护级别:保护品种");
//        }
//        if (nameList.get(0).getRare() == 0) {
//            raretext.setText("稀有级别:较常见");
//        } else {
//            raretext.setText("稀有级别:较稀有");
//        }
//        if (nameList.get(0).getUniqueToChina() == 0) {
//            uniqueToChinatext.setText("中国特有:分布广泛");
//        } else {
//            uniqueToChinatext.setText("中国特有:分布不广泛");
//        }

//        fab_fruit_content.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(InfoActivity.this, MainActivity.class);
//                startActivity(intent);
//                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//            }
//        });

//        if (imageList.size() > 1) {
//            vp.setOnPageChangeListener(new ViewPagerIndicator(this, vp, ll, imageList.size()));
//        } else {
//            vp.toggleSlide(false);//若该类蝴蝶只有一张图片则不进行切换显示
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}
