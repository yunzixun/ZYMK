package top.wzmyyj.zymk.view.panel;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import top.wzmyyj.wzm_sdk.adapter.ivd.IVD;
import top.wzmyyj.wzm_sdk.adapter.ivd.SingleIVD;
import top.wzmyyj.zymk.R;
import top.wzmyyj.zymk.app.bean.BoBean;
import top.wzmyyj.zymk.app.bean.BookBean;
import top.wzmyyj.zymk.app.bean.ItemBean;
import top.wzmyyj.zymk.app.utils.GlideImageLoader;
import top.wzmyyj.zymk.common.utils.DensityUtil;
import top.wzmyyj.zymk.common.utils.StatusBarUtil;
import top.wzmyyj.zymk.presenter.HomePresenter;
import top.wzmyyj.zymk.view.adapter.BookAdapter;
import top.wzmyyj.zymk.view.panel.base.BaseRecyclerPanel;


/**
 * Created by yyj on 2018/07/04. email: 2209011667@qq.com
 * 主页。已改成HomeNestedScrollPanel代替。此类不用了。
 */

public class HomeRecyclerPanel extends BaseRecyclerPanel<ItemBean, HomePresenter> {

    public HomeRecyclerPanel(Context context, HomePresenter p) {
        super(context, p);
    }

    @Override
    protected void setData() {
        mPresenter.addEmptyData(mData);
    }


    @Override
    protected void initView() {
        super.initView();
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            int mDistance = 0;
            //当距离在[0,maxDistance]变化时，透明度在[0,255之间变化]
            int maxDistance = DensityUtil.dp2px(context, 155) - StatusBarUtil.StatusBarHeight;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (viewList.size() == 0) return;
                View top = viewList.get(0);

                mDistance += dy;
                float percent = mDistance * 1f / maxDistance;//百分比


                top.setAlpha(percent);
            }
        });
    }


    @Override
    protected void setIVD(List<IVD<ItemBean>> ivd) {
        ivd.add(new SingleIVD<ItemBean>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.layout_home_item;
            }

            @Override
            public void convert(ViewHolder holder, ItemBean itemBean, int position) {

                ImageView img_icon = holder.getView(R.id.img_icon);
                TextView tv_title = holder.getView(R.id.tv_title);
                TextView tv_summary = holder.getView(R.id.tv_summary);
                img_icon.setImageResource(itemBean.getIcon());
                tv_title.setText(itemBean.getTitle());
                tv_summary.setText(itemBean.getSummary());

                final String href = itemBean.getHref();
                Button bt_more = holder.getView(R.id.bt_more);
                bt_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.goMore(href);
                    }
                });

                final List<BookBean> data = itemBean.getBooks() != null
                        ? itemBean.getBooks() : new ArrayList<BookBean>();


                RecyclerView rv_item = holder.getView(R.id.rv_item);
                rv_item.setRecycledViewPool(viewPool);
                rv_item.setLayoutManager(new LinearLayoutManager(context, LinearLayout.HORIZONTAL, false));
                BookAdapter bookAdapter = new BookAdapter(context, R.layout.layout_book, data);
                rv_item.setAdapter(bookAdapter);


            }

            // 共用view池。
            RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();


        });
    }


    @Override
    public void update() {
        mPresenter.loadData();
    }

    @Override
    public Object f(int w, Object... objects) {
        if (w == -1) return null;
        List<BoBean> bos = (List<BoBean>) objects[0];
        setBanner(bos);
        mData.clear();
        List<ItemBean> itemBeans = (List<ItemBean>) objects[1];
        for (ItemBean item : itemBeans) {
            mData.add(item);
        }
        notifyDataSetChanged();
        return super.f(w, objects);
    }

    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        mBanner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        mBanner.stopAutoPlay();
    }

    protected Banner mBanner;

    @Override
    protected void setHeader() {
        super.setHeader();
        mHeader = mInflater.inflate(R.layout.layout_home_header, null);
        LinearLayout ll_h_1 = mHeader.findViewById(R.id.ll_h_1);
        LinearLayout ll_h_2 = mHeader.findViewById(R.id.ll_h_2);
        ll_h_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.goNew();
            }
        });
        ll_h_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.goRank();
            }
        });

        List images = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        mBanner = mHeader.findViewById(top.wzmyyj.wzm_sdk.R.id.banner);
        //设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        mBanner.setImages(images);
        //设置banner动画效果
        mBanner.setBannerAnimation(Transformer.Accordion);
        //设置标题集合（当banner样式有显示title时）
        mBanner.setBannerTitles(titles);
        //设置自动轮播，默认为true
        mBanner.isAutoPlay(true);
        //设置轮播时间
        mBanner.setDelayTime(5000);
        //设置banner样式
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //设置指示器位置（当banner模式中有指示器时）
        mBanner.setIndicatorGravity(BannerConfig.RIGHT);
        //banner设置方法全部调用完毕时最后调用
        mBanner.start();


    }

    public void setBanner(final List<BoBean> bos) {
        if (bos == null || bos.size() < 6) return;
        List<String> imgs = new ArrayList<>();
        List<String> strs = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            BoBean bo = bos.get(i);
            imgs.add(bo.getData_src());
            strs.add(bo.getTitle());
        }
        mBanner.update(imgs, strs);

        mBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                mPresenter.goDetails(bos.get(position).getHref());
            }
        });
    }

    @Override
    protected void setFooter() {
        super.setFooter();
        mFooter = mInflater.inflate(R.layout.layout_footer, null);
        TextView tv = mFooter.findViewById(R.id.tv_end);
        tv.setText("-- 到底部了哦 --");
    }
}
