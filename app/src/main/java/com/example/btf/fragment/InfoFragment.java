package com.example.btf.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.btf.MainActivity;
import com.example.btf.R;
import com.example.btf.db.InfoDetail;
import com.example.btf.viewpage.GuardViewPager;
import com.example.btf.viewpage.VPAdapter;
import com.example.btf.viewpage.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private Context context;

    FloatingActionButton fab_fruit_content;
    TextView nametext;
    TextView latinNametext;
    TextView typetext;
    TextView featuretext;
    ;
    TextView areatext;
    TextView protecttext;
    TextView raretext;
    TextView uniqueToChinatext;

    private GuardViewPager vp;
    private VPAdapter vpAdapter;
    private LinearLayout ll;

    public InfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.info_fragment, container, false);
        return view;
    }

    public void refresh(final Context context, List<InfoDetail> nameList, ArrayList<String> imageList) {
        this.context = context;
        vp = (GuardViewPager) view.findViewById(R.id.vp);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        vpAdapter = new VPAdapter(context, fm, imageList, nameList.get(0));
        vp.setAdapter(vpAdapter);
        ll = (LinearLayout) view.findViewById(R.id.ll);
        fab_fruit_content = (FloatingActionButton) view.findViewById(R.id.fab_fruit_content);

        nametext = (TextView) view.findViewById(R.id.name);
        latinNametext = (TextView) view.findViewById(R.id.latinName);
        typetext = (TextView) view.findViewById(R.id.type);
        featuretext = (TextView) view.findViewById(R.id.feature);
        areatext = (TextView) view.findViewById(R.id.area);
        protecttext = (TextView) view.findViewById(R.id.protect);
        raretext = (TextView) view.findViewById(R.id.rare);
        uniqueToChinatext = (TextView) view.findViewById(R.id.uniqueToChina);

        if (nameList.get(0).getName() != null)
            nametext.setText("中文学名:" + nameList.get(0).getName());
        else
            nametext.setText("中文学名:暂无");
        if (nameList.get(0).getLatinName() != null)
            latinNametext.setText("拉丁学名:" + nameList.get(0).getLatinName());
        else
            latinNametext.setText("拉丁学名:暂无");
        if (nameList.get(0).getType() != null)
            typetext.setText("科属:" + nameList.get(0).getType());
        else
            typetext.setText("科属:暂无");
        if (nameList.get(0).getFeature() != null)
            featuretext.setText("识别特征:\n" + nameList.get(0).getFeature());
        else
            featuretext.setText("识别特征:\n暂无");
        if (nameList.get(0).getArea() != null)
            areatext.setText("地理分布:\n" + nameList.get(0).getArea());
        else
            areatext.setText("地理分布:\n暂无");
        if (nameList.get(0).getProtect() == 0) {
            protecttext.setText("保护级别:非保护品种");
        } else {
            protecttext.setText("保护级别:保护品种");
        }
        if (nameList.get(0).getRare() == 0) {
            raretext.setText("稀有级别:较常见");
        } else {
            raretext.setText("稀有级别:较稀有");
        }
        if (nameList.get(0).getUniqueToChina() == 0) {
            uniqueToChinatext.setText("中国特有:分布广泛");
        } else {
            uniqueToChinatext.setText("中国特有:分布不广泛");
        }

        fab_fruit_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                Intent intent = new Intent(InfoActivity.this, MainActivity.class);
                //                startActivity(intent);
                //                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                actionStart(context);
            }
        });

        if (imageList.size() > 1) {
            vp.setOnPageChangeListener(new ViewPagerIndicator(context, vp, ll, imageList.size()));
        } else {
            vp.toggleSlide(false);//若该类蝴蝶只有一张图片则不进行切换显示
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //                finish();
                //                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
