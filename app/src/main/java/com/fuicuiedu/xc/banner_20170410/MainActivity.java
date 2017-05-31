package com.fuicuiedu.xc.banner_20170410;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TextView title;//显示图片标题
    private List<ImageView> imageViews;//图片的集合
    private List<View> dots;//圆点的集合
    private int oldPosition;
    private int currentItem;

    private ScheduledExecutorService scheduledExecutorService;//用来定时轮播

    //存放图片id
    private int[] imageIds = new int[]{
            R.drawable.gwx,
            R.drawable.lc,
            R.drawable.ljx,
            R.drawable.tz,
            R.drawable.xll,
    };

    //存放图片的标题
    private String[] titles = new String[]{
            "我是郭文鑫",
            "我是黎超",
            "我是陆建鑫",
            "我是汤志",
            "我是小磊磊"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.vp);

        imageViews = new ArrayList<>();
        for (int i = 0; i < imageIds.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(imageIds[i]);
            imageViews.add(imageView);
        }

        dots = new ArrayList<>();
        dots.add(findViewById(R.id.dot_0));
        dots.add(findViewById(R.id.dot_1));
        dots.add(findViewById(R.id.dot_2));
        dots.add(findViewById(R.id.dot_3));
        dots.add(findViewById(R.id.dot_4));

        title = (TextView) findViewById(R.id.my_title);
        title.setText(titles[0]);

        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);

        //完成标题和圆点的改变
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                title.setText(titles[position]);

                dots.get(position).setBackgroundResource(R.drawable.dot_focused);
                dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);

                oldPosition = position;

                currentItem = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开一个后台线程
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //给线程池添加一个“定时调度任务”
        //  延迟initialDelay时间后开始执行command，
        // 并且按照period时间周期性重复调用（周期时间包括command运行时间，
        // 如果周期时间比command运行时间断，则command运行完毕后，立刻重复运行））
        scheduledExecutorService.scheduleWithFixedDelay(
                new ViewpagerTask(),
                2,
                2,
                TimeUnit.SECONDS
        );
    }

    private class ViewpagerTask implements Runnable{
        @Override
        public void run() {
            //取余来实现轮播
            currentItem = (currentItem + 1) % imageIds.length;
            mHandler.sendEmptyMessage(0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (scheduledExecutorService != null){
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //第二个参数设置为false，不要滑动效果
            viewPager.setCurrentItem(currentItem,false);
        }
    };

    private class ViewPagerAdapter extends PagerAdapter {
        //获取当前窗体数量
        @Override
        public int getCount() {
            return imageIds.length;
        }

        //判断是否由对象生成界面
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //return一个对象，这个对象表明适配器选择哪个对象放在当前的界面中
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imageViews.get(position));
            return imageViews.get(position);
        }

        //是从ViewGroup移除当前的view
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViews.get(position));
        }
    }
}
