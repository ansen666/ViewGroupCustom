package ansen.viewgroupcustom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class MySingleLineLayout extends ViewGroup {
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    private int gravity;

    public MySingleLineLayout(Context context) {
        this(context,null);
    }

    public MySingleLineLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        //获取自定义属性的值
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.sigleLine);
        gravity=typedArray.getInt(R.styleable.sigleLine_gravity,0);
        typedArray.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i("ansen","onLayout gravity:"+gravity);
        int firstHeight=0;//第一个View的高度
        if(gravity==LEFT){//左边
            int left=0;
            for(int i=0;i<getChildCount();i++){
                View child=getChildAt(i);
                MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
                if(i==0){
                    firstHeight=child.getMeasuredHeight();
                    Log.i("ansen","getMeasuredWidth:"+child.getMeasuredWidth());
                    child.layout(getPaddingLeft()+params.leftMargin,getPaddingTop(),child.getMeasuredWidth()+params.leftMargin+params.rightMargin,getPaddingTop()+child.getMeasuredHeight());
                }else{
                    int top=(firstHeight-child.getMeasuredHeight())/2;
                    child.layout(left+params.leftMargin,getPaddingTop()+params.topMargin+top,left+child.getMeasuredWidth()+params.leftMargin,getPaddingTop()+child.getMeasuredHeight()+params.topMargin+top);
                }
                left+=child.getMeasuredWidth() + getPaddingLeft()+params.leftMargin+params.rightMargin;
            }
        }else{//右边
            int right=0;
            for(int i=getChildCount()-1;i>=0;i--){
                View child=getChildAt(i);
                MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
                if(i==getChildCount()-1){
                    firstHeight=child.getMeasuredHeight();
                    child.layout(getWidth()-(getPaddingLeft()+params.leftMargin+child.getMeasuredWidth()),getPaddingTop(),getWidth()+params.rightMargin,getPaddingTop()+child.getMeasuredHeight());
                }else{
                    Log.i("ansen","left:"+(getWidth()-right-child.getMeasuredWidth()-params.leftMargin)+" right:"+(getWidth()-right+params.rightMargin)+"child.getWidth():"+child.getMeasuredWidth());
                    int top=(firstHeight-child.getMeasuredHeight())/2;//(第一行的高度-测量高度)/2=顶部边距
                    child.layout(getWidth()-right-child.getMeasuredWidth()-params.rightMargin,getPaddingTop()+params.topMargin+top,getWidth()-right-params.rightMargin,getPaddingTop()+child.getMeasuredHeight()+params.topMargin+top);
                }
                right+=child.getMeasuredWidth()+params.leftMargin+params.rightMargin+getPaddingLeft()+getPaddingRight();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);//获取ViewGroup的宽度
        Log.i("ansen","gravity:"+gravity);
        //未指定模式 父元素不对子元素施加任何束缚，子元素可以得到任意想要的大小
        int unspecifiedMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int firstWidth=width;
        View firstView=null;
        if(gravity == LEFT){
            for(int i=0;i<getChildCount();i++){
                View child=getChildAt(i);
                MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
                Log.i("ansen","i:"+i);

                if(i==0){
                    firstView=child;
                    firstWidth-=(params.leftMargin+params.rightMargin);
                }else{
                    if(child.getVisibility()!=View.GONE){//必须是占用空间的View
                        child.measure(unspecifiedMeasureSpec,unspecifiedMeasureSpec);
                        firstWidth -= (child.getMeasuredWidth()+getPaddingLeft()+getPaddingRight()+params.leftMargin+params.rightMargin);//第一个View可以显示的最大宽度
                    }
                }
            }
        }else{
            for(int i=getChildCount()-1;i>=0;i--){
                View child=getChildAt(i);
                MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
                Log.i("ansen","i:"+i);

                if(i==getChildCount()-1){
                    firstView=child;
                    firstWidth-=(params.leftMargin+params.rightMargin);
                }else{
                    if(child.getVisibility()!=View.GONE){//必须是占用空间的View
                        child.measure(unspecifiedMeasureSpec,unspecifiedMeasureSpec);
                        firstWidth -= (child.getMeasuredWidth()+getPaddingLeft()+getPaddingRight()+params.leftMargin+params.rightMargin);//第一个View可以显示的最大宽度
                    }
                }
            }
        }
        Log.i("ansen","firstWidth:"+firstWidth);
        int maxWidthMeasureSpec = MeasureSpec.makeMeasureSpec(firstWidth,MeasureSpec.AT_MOST);
        firstView.measure(maxWidthMeasureSpec,unspecifiedMeasureSpec);
        int height = getPaddingBottom() + getPaddingTop() + firstView.getMeasuredHeight();

        Log.i("ansen","width:"+width+" height:"+height);
        setMeasuredDimension(width,height);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(),attrs);
    }
}
