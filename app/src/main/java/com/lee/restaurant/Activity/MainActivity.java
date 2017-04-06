package com.lee.restaurant.Activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lee.restaurant.Model.Dish;
import com.lee.restaurant.Model.Dishes;
import com.lee.restaurant.R;
import com.lee.restaurant.Service.DataService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import zxing.CaptureActivity;

/**
 * 主界面
 *
 *
 */
public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;

    @InjectView(R.id.main_recyclerview_kind)
    RecyclerView recyclerViewKind;

    RecyclerViewAdapter adapter;
    KindRecycleViewAdapter kindRecycleViewAdapter;

    public static ArrayList<Dishes.KindEntity.DishEntity> mainData;

    public static Map<String,Dish> cart;
    public static ArrayList<String> cartList;

    private boolean needMove = false;
    private int movedPosition;
    private int selectedPosition = 0;
    private boolean fingerTouch = false;

    public static boolean QRorNot = false;

    private String tableNo = null;



    private ArrayList<Dishes.KindEntity.DishEntity> recommandList;
    ImageView[] imageViews;

    @InjectView(R.id.toolBar)
    Toolbar toolbar;

    @InjectView(R.id.collapsingToolBar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @InjectView(R.id.fab)
    FloatingActionButton fab;

    @InjectView(R.id.navigation_view)
    NavigationView navigationView;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @InjectView(R.id.content_view_pager)
    ViewPager viewPager;

    Handler handler;

    public static ImageLoader imageLoader;
    ImageLoaderConfiguration imageLoaderConfiguration;
    public static DisplayImageOptions displayImageOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }

        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);


        setUpActionBar();
        setUpDrawerContent(navigationView);

        initImageLoader();
        collapsingToolbarLayout.setTitle(" ");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,
                toolbar,0,0);
        toggle.syncState();

        setUpFab();

        getData();

        cart = new HashMap<>();
        cartList = new ArrayList<>();

    }

    private void setUpViewPager(Dishes dishes){
        recommandList = new ArrayList<>();
        for(int i = 0;i< dishes.kinds.size();i++){
            int j = new Random().nextInt(dishes.kinds.get(i).dishes.size());
            recommandList.add(dishes.kinds.get(i).dishes.get(j));
        }

        imageViews = new ImageView[recommandList.size()];
        for (int i = 0;i<recommandList.size();i++){
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageLoader.displayImage(recommandList.get(i).path,imageView,displayImageOptions);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this,DetialActivity.class);
                    intent.putExtra("dish",recommandList.get(viewPager.getCurrentItem() % recommandList.size()));
                    startActivity(intent);
                }
            });
            imageViews[i] = imageView;
        }

        viewPager.setAdapter(new PagerAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(imageViews.length * 100);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 123:
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        viewPager.invalidate();
                        handler.sendEmptyMessageDelayed(123,2500);
                        break;
                }
            }
        };

        handler.sendEmptyMessageDelayed(123,0);
    }

    private void setUpActionBar(){
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        bar.setHomeAsUpIndicator(R.drawable.menu);
        bar.setDisplayHomeAsUpEnabled(true);
    }

    private void initImageLoader(){
        imageLoader = ImageLoader.getInstance();
        imageLoaderConfiguration = ImageLoaderConfiguration.createDefault(getApplicationContext());
        imageLoader.init(imageLoaderConfiguration);
        displayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .displayer(new SimpleBitmapDisplayer())
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    private void setUpFab(){
        if (fab != null){
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this,CartActivity.class);
                    if (tableNo != null)
                        intent.putExtra("tableNo",tableNo);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            String QRResult = data.getStringExtra("QRResult");
            if (QRResult.startsWith("http://123.206.221.174/")){
                tableNo = QRResult.split("tableNo=")[1];
                QRorNot = true;
                Toast.makeText(this,"扫码成功！",Toast.LENGTH_SHORT).show();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("请扫描正确的二维码!");
                builder.setPositiveButton("好", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        }
    }

    private void setUpDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                drawerLayout.closeDrawers();
                Intent intent;
                switch (item.getItemId()){
                    case R.id.person:            //登录

                        if(IsHaveUser()){

                            Toast.makeText(getApplicationContext(),"您已经登录",Toast.LENGTH_LONG).show();

                        }else{
                            intent = new Intent(MainActivity.this,LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case R.id.chagemessage:    //更改个人信息
                        if( !IsHaveUser())
                        {
                            Toast.makeText(getApplicationContext(),"请先登录",Toast.LENGTH_LONG).show();

                        }else{
                            intent = new Intent(MainActivity.this,ChangeActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case R.id.lastorder:             //历史订单

                        if( !IsHaveUser())
                        {
                            Toast.makeText(getApplicationContext(),"请先登录",Toast.LENGTH_LONG).show();

                        }else{
                            intent = new Intent(MainActivity.this,PastActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case R.id.main_menu:        //菜单
                        break;
                    case R.id.cart:           //已选菜品
                        intent = new Intent(MainActivity.this,CartActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.qrcode:            // 二维码
                        if( IsHaveUser())
                        {
                            Toast.makeText(getApplicationContext(),"如果您在店内就餐，请先退出登录",Toast.LENGTH_LONG).show();

                        }else {
                            intent = new Intent(MainActivity.this, CaptureActivity.class);
                            intent.putExtra("requestCode",1);
                            startActivityForResult(intent,1);
                        }

                        break;

                    case R.id.quit_user:          //退出当前用户

                        SharedPreferences preferences = getSharedPreferences("user",Activity.MODE_PRIVATE);
                        String phone = preferences.getString("phone","");
                        if(!phone.equals(""))
                        {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("name","");
                            editor.putString("pass","");
                            editor.putString("address","");
                            editor.putString("phone","");
                            editor.commit();

                            Toast.makeText(getApplicationContext(),"已退出当前用户",Toast.LENGTH_LONG).show();
                        }else {

                            Toast.makeText(getApplicationContext(),"未登录",Toast.LENGTH_LONG).show();
                        }
                        break;

                    case R.id.about_us:
                        intent = new Intent(MainActivity.this,AboutActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    /**
     *  判断当前用户是否存在
     * @return   如果存在用户，则返回true，没有用户，则返回 false
     */
    private boolean IsHaveUser() {

        SharedPreferences preference = getSharedPreferences("user", Activity.MODE_PRIVATE);

        String phone = preference.getString("phone","");

        if(!phone.equals(""))
        {
            return true;
        }
        return  false;
    }


    private void moveToPosition(RecyclerView recyclerView,int position){
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstPosition = manager.findFirstVisibleItemPosition();
        int lastPosition = manager.findLastVisibleItemPosition();
        if (position <= firstPosition){
            recyclerView.smoothScrollToPosition(position);
        }else if (position <= lastPosition){
            int top = recyclerView.getChildAt(position - firstPosition).getTop();
            recyclerView.smoothScrollBy(0,top);
        }else if(position > lastPosition){
            recyclerView.smoothScrollToPosition(position);
            needMove = true;
            movedPosition = position;
        }
    }


    private void getData(){
        Subscriber<Dishes> subscriber = new Subscriber<Dishes>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                Log.d("-----------",e.getMessage());
            }
            @Override
            public void onNext(Dishes dishes) {
                updataUI(dishes);
            }
        };
        DataService.getInstance().get(subscriber);
    }

    private void updataUI(Dishes dishes){
        setUpViewPager(dishes);

        adapter = new RecyclerViewAdapter(getApplicationContext(),dishes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new ScrollListener(imageLoader));
        recyclerView.addItemDecoration(new RecyclerViewDecoration());
        recyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(adapter));

        kindRecycleViewAdapter = new KindRecycleViewAdapter(getApplicationContext(),dishes);
        recyclerViewKind.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewKind.setAdapter(kindRecycleViewAdapter);
        recyclerViewKind.addItemDecoration(new RecyclerViewDecoration());
    }

    class ScrollListener extends RecyclerView.OnScrollListener{

        ImageLoader imageLoader;

        public ScrollListener(ImageLoader imageLoader) {
            this.imageLoader = imageLoader;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (recyclerView.getId() == R.id.recyclerView){
                switch (newState){
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        imageLoader.pause();
                        fingerTouch = false;
                        kindRecycleViewAdapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_IDLE:
                        imageLoader.resume();
                        fingerTouch = false;
                        kindRecycleViewAdapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        fingerTouch = true;
                        kindRecycleViewAdapter.notifyDataSetChanged();
                        break;
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (needMove){
                needMove = false;
                int n = movedPosition - ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                if (0 <= n && n < recyclerView.getChildCount()){
                    int top = recyclerView.getChildAt(n).getTop();
                    recyclerView.smoothScrollBy(0,top);
                }
            }

            int position = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

            boolean last = false;
            if (((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition() == (adapter.data.size() - 1)){
                selectedPosition = kindRecycleViewAdapter.listKind.size() - 1;
                kindRecycleViewAdapter.notifyDataSetChanged();
                recyclerViewKind.smoothScrollToPosition(selectedPosition);
                last = true;
            }
            if (adapter.getKindType(position) != selectedPosition && !last && !fingerTouch){
                selectedPosition = adapter.getKindType(position);
                kindRecycleViewAdapter.notifyDataSetChanged();
                recyclerViewKind.smoothScrollToPosition(selectedPosition);
            }

            last = false;
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerViewAdapter.HeaderViewHolder>{

        Context context;
        Dishes dishes;
        public List<Dishes.KindEntity.DishEntity> data;

        public RecyclerViewAdapter(Context context,Dishes dishes) {
            this.context = context;

            this.dishes = dishes;
            ArrayList<Dishes.KindEntity.DishEntity> data = new ArrayList<>();
            for (int i = 0;i < dishes.kinds.size();i++){
                data.addAll(dishes.kinds.get(i).dishes);
            }

            mainData = data;
            this.data = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.textViewName.setText(data.get(position).name);
            holder.textViewPrice.setText(data.get(position).price);
            imageLoader.displayImage(data.get(position).path,holder.imageView,displayImageOptions);
            holder.button.setTag(position);
            holder.setOnClickListener(new OnItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Intent intent = new Intent(MainActivity.this,DetialActivity.class);
                    intent.putExtra("dish",data.get(position));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startActivityForResult(intent,1,ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,holder.imageView,"imageview").toBundle());
                    }else {
                        startActivityForResult(intent,1);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public long getHeaderId(int position) {
            return getKindType(position);
        }

        public int getItemPosition(int kind){
            int position = 0;
            for (int i = 0;i < kind;i++){
                position += dishes.kinds.get(i).dishes.size();
            }
            return position;
        }

        public int getKindType(int position){
            int sort = -1;
            int sum = 0;
            for(int i = 0;i < data.size();i++){
                if (position >= sum){
                    sort++;
                }else{
                    return sort;
                }
                sum += dishes.kinds.get(i).dishes.size();
            }
            return sort;
        }

        @Override
        public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.recycler_sticky_header,parent,false);
            HeaderViewHolder headerViewHolder = new HeaderViewHolder(view);
            return headerViewHolder;
        }

        @Override
        public void onBindHeaderViewHolder(HeaderViewHolder holder, int position) {
            holder.textViewHeader.setText(dishes.kinds.get(getKindType(position)).name);
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            ImageView imageView;
            TextView textViewName,textViewPrice;
            Button button;

            private OnItemClickListener listener;

            public ViewHolder(View itemView) {
                super(itemView);
                textViewName = (TextView) itemView.findViewById(R.id.list_tv_name);
                textViewPrice = (TextView) itemView.findViewById(R.id.list_tv_price);
                imageView = (ImageView) itemView.findViewById(R.id.list_iv);
                button = (Button) itemView.findViewById(R.id.btnAdd);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = adapter.data.get((Integer) view.getTag()).name;
                        if (cart.containsKey(name)){
                            cart.get(name).setCount(cart.get(name).getCount() + 1);
                        }else{
                            Dish dish = new Dish(adapter.data.get((Integer) view.getTag()));
                            dish.setCount(1);
                            cart.put(name,dish);
                            cartList.add(name);
                        }
                        Toast.makeText(getApplicationContext(),"添加成功",Toast.LENGTH_SHORT).show();
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null){
                            listener.onClick(view,getLayoutPosition());
                        }
                    }
                });
            }

            public void setOnClickListener(OnItemClickListener listener){
                this.listener = listener;
            }
        }

        class HeaderViewHolder extends RecyclerView.ViewHolder{

            TextView textViewHeader;

            public HeaderViewHolder(View itemView) {
                super(itemView);
                textViewHeader = (TextView) itemView.findViewById(R.id.header_tv);
            }
        }
    }

    class KindRecycleViewAdapter extends RecyclerView.Adapter<KindRecycleViewAdapter.KindViewHolder>{

        Context context;
        public ArrayList<Dishes.KindEntity> listKind;

        public KindRecycleViewAdapter(Context context,Dishes dishes) {
            this.context = context;

            ArrayList<Dishes.KindEntity> kindList = new ArrayList<>();
            kindList.addAll(dishes.kinds);

            this.listKind = kindList;
        }

        @Override
        public KindViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_layout_kind,parent,false);
            KindViewHolder holder = new KindViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(KindViewHolder holder, int position) {
            holder.textViewKind.setText(listKind.get(position).name);
            holder.setOnClickListener(new OnItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    recyclerViewKind.smoothScrollToPosition(position);
                    moveToPosition(recyclerView,adapter.getItemPosition(position));
                    selectedPosition = position;
                    fingerTouch = true;
                }
            });
            if (position == selectedPosition){
                holder.relativeLayout.setBackgroundColor(Color.WHITE);
                holder.viewLine.setVisibility(View.VISIBLE);
            }else{
                holder.relativeLayout.setBackgroundColor(Color.rgb(0xe0,0xe0,0xe0));
                holder.viewLine.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return listKind.size();
        }

        class KindViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            private OnItemClickListener listener;

            RelativeLayout relativeLayout;
            TextView textViewKind;
            View viewLine;

            public KindViewHolder(View itemView) {
                super(itemView);
                textViewKind = (TextView) itemView.findViewById(R.id.kind_rv_tv);
                viewLine = itemView.findViewById(R.id.kind_rv_line);
                relativeLayout = (RelativeLayout) itemView.findViewById(R.id.kind_relative_layout);
                itemView.setOnClickListener(this);
            }

            public void setOnClickListener(OnItemClickListener listener){
                this.listener = listener;
            }

            @Override
            public void onClick(View view) {
                if (listener != null){
                    listener.onClick(view,getLayoutPosition());
                }
            }
        }

    }

    public interface OnItemClickListener{
        void onClick(View view,int position);
    }

    class PagerAdapter extends android.support.v4.view.PagerAdapter{

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ViewGroup viewGroup = (ViewGroup)imageViews[position % imageViews.length].getParent();
            if (viewGroup != null){
                viewGroup.removeAllViews();
            }
            container.addView(imageViews[position % imageViews.length],0);
            return imageViews[position % imageViews.length];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViews[position % imageViews.length]);
        }
    }
}
