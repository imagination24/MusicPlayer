package com.laizhenghuo.media;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

public class LycicView extends ScrollView {
    LinearLayout rootView;//父布局
    LinearLayout lycicList;//垂直布局
    ArrayList<TextView> lyricItems = new ArrayList<TextView>();//每项的歌词集合

    ArrayList<String> lyricTextList = new ArrayList<String>();//每行歌词文本集合，建议先去看看手机音乐里的歌词格式和内容
    ArrayList<Long> lyricTimeList = new ArrayList<Long>();//每行歌词所对应的时间集合
    ArrayList<Integer> lyricItemHeights;//每行歌词TextView所要显示的高度

    int height;//控件高度
    int width;//控件宽度
    int prevSelected = 0;//前一个选择的歌词所在的item


    public LycicView(Context context) {
        super(context);
        init();
    }

    public LycicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LycicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        rootView = new LinearLayout(getContext());
        rootView.setOrientation(LinearLayout.VERTICAL);
        //创建视图树，会在onLayout执行后立即得到正确的高度等参数
        ViewTreeObserver vto = rootView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                height = com.laizhenghuo.media.LycicView.this.getHeight();
                width = com.laizhenghuo.media.LycicView.this.getWidth();

                refreshRootView();

            }
        });
        addView(rootView);//把布局加进去
    }

    /**
     *
     */
    void refreshRootView(){
        rootView.removeAllViews();//刷新，先把之前包含的所有的view清除
        //创建两个空白view
        LinearLayout blank1 = new LinearLayout(getContext());
        LinearLayout blank2 = new LinearLayout(getContext());
        //高度平分
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,height/2);
        rootView.addView(blank1,params);
        if(lycicList !=null){
            rootView.addView(lycicList);//加入一个歌词显示布局
            rootView.addView(blank2,params);
        }

    }

    /**
     *设置歌词,
     */
    void refreshLyicList(){
        if(lycicList == null){
            lycicList = new LinearLayout(getContext());
            lycicList.setOrientation(LinearLayout.VERTICAL);
            //刷新，重新添加
            lycicList.removeAllViews();
            lyricItems.clear();
            lyricItemHeights = new ArrayList<Integer>();
            prevSelected = 0;
            //为每行歌词创建一个TextView
            for(int i = 0;i<lyricTextList.size();i++){
                final TextView textView = new TextView(getContext());

                textView.setText(lyricTextList.get(i));
                //居中显示
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                textView.setLayoutParams(params);
                //对高度进行测量
                ViewTreeObserver vto = textView.getViewTreeObserver();
                final int index = i;
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);//api 要在16以上 >=16
                        lyricItemHeights.add(index,textView.getHeight());//将高度添加到对应的item位置
                    }
                });
                lycicList.addView(textView);
                lyricItems.add(index,textView);
            }
        }
    }
    /**
     *     滚动到index位置
     */
    public void scrollToIndex(int index){
        if(index < 0){
            scrollTo(0,0);
        }
        //计算index对应的textview的高度
        if(index < lyricTextList.size()){
            int sum = 0;
            for(int i = 0;i<=index-1;i++){
                sum+=lyricItemHeights.get(i);
            }
            //加上index这行高度的一半
            sum+=lyricItemHeights.get(index)/2;
            scrollTo(0,sum);
        }
    }

    /**
     * 歌词一直滑动，小于歌词总长度
     * @param length
     * @return
     */

    int getIndex(int length){
        int index = 0;
        int sum = 0;
        while(sum <= length){
            sum+=lyricItemHeights.get(index);
            index++;
        }
        //从1开始，所以得到的是总item，脚标就得减一
        return index - 1;
    }

    /**
     * 设置选择的index，选中的颜色
     * @param index
     */
    void setSelected(int index){
        //如果和之前选的一样就不变
        if(index == prevSelected){
            return;
        }
        for(int i = 0;i<lyricItems.size();i++){
            //设置选中和没选中的的颜色
            if(i == index){
                lyricItems.get(i).setTextColor(Color.BLUE);
            }else{
                lyricItems.get(i).setTextColor(Color.WHITE);
            }
            prevSelected = index;
        }
    }

    /**
     * 设置歌词，并调用之前写的refreshLyicList()方法设置view
     * @param textList
     * @param timeList
     */
    public void setLyricText(ArrayList<String> textList,ArrayList<Long> timeList){
        //因为你从歌词lrc里面可以看出，每行歌词前面都对应有时间，所以两者必须相等
        if(textList.size() != timeList.size()){
            throw  new IllegalArgumentException();
        }
        this.lyricTextList = textList;
        this.lyricTimeList = timeList;

        refreshLyicList();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //滑动时，不往回弹，滑到哪就定位到哪
        setSelected(getIndex(t));
        if(listener != null){
            listener.onLyricScrollChange(getIndex(t),getIndex(oldt));
        }
    }
    OnLyricScrollChangeListener listener;
    public void setOnLyricScrollChangeListener(OnLyricScrollChangeListener l){
        this.listener = l;
    }

    /**
     * 向外部提供接口
     */
    public interface  OnLyricScrollChangeListener{
        void onLyricScrollChange(int index,int oldindex);
    }
}
