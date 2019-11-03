package com.example.gp4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.internal.bind.ArrayTypeAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// 나의 향 정보
public class MyFragment extends Fragment {

    private Boolean isWork = false; // 디퓨저가 작동 중인지 저장할 변수
    private SharedPreferences sharedPreferences;
    private String key; // 저장되어 있는 사용자의 pushId 정보 저장할 변수

    private RecyclerView recyclerView;

    private ArrayList keyList = new ArrayList<>(); // 향 정보의 키 값을 저장하기 위한 배열 - 삭제를 위해 필요

    private TotalInfo nowInfo = new TotalInfo(); // 현재 저장되어 있는 향 정보 저장할 변수

    public static MyFragment newInstance(){
        return new MyFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        key = sharedPreferences.getString("pushID", ""); // 저장되어 있는 키 값 불러오기

        View view = inflater.inflate(R.layout.fragment_my, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.fragment_my_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new MyFragmentRecyclerViewAdapter());

        // 스와이프 해서 삭제
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        isWork = sharedPreferences.getBoolean("turnOn", false); // 디퓨저 작동하는지 가져오기

        init(); // 현재 저장되어 있는 향의 이름 정보 가져오기

        return view;
    }


    // 스와이프 해서 삭제
    private ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            final int position = viewHolder.getAdapterPosition();
            FirebaseDatabase.getInstance().getReference("storage")
                    .child(key).child(keyList.get(position).toString()).removeValue(); // 파이어 베이스 데이터 삭제
            keyList.remove(position);
        }
    };

    class MyFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<TotalInfo> totalInfos;

        public MyFragmentRecyclerViewAdapter(){

            totalInfos = new ArrayList<>();

            ValueEventListener valueEventListener = FirebaseDatabase.getInstance().getReference("storage").child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    totalInfos.clear(); // 누적된 데이터 클리어
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        keyList.add(snapshot.getKey()); // 키 값 넣기 - 나중에 삭제를 위해 필요
                        TotalInfo tmptotal = snapshot.getValue(TotalInfo.class);
                        totalInfos.add(tmptotal); // 리스트에 추가
                    }
                    notifyDataSetChanged(); //새로고침
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_scentinfo, viewGroup, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {

            //텍스트 올리기
            ((CustomViewHolder)viewHolder).nameview.setText(totalInfos.get(i).getName());

            String string = totalInfos.get(i).getCatridgeInfo1().getName() + " : " + whatString(totalInfos.get(i).getCatridgeInfo1().getRest())
                    +" " + totalInfos.get(i).getCatridgeInfo2().getName() + " : " + whatString(totalInfos.get(i).getCatridgeInfo2().getRest())
                    +" " + totalInfos.get(i).getCatridgeInfo3().getName() + " : " + whatString(totalInfos.get(i).getCatridgeInfo3().getRest())
                    +"\n" + totalInfos.get(i).getCatridgeInfo4().getName() + " : " + whatString(totalInfos.get(i).getCatridgeInfo4().getRest())
                    +" " + totalInfos.get(i).getCatridgeInfo5().getName() + " : " + whatString(totalInfos.get(i).getCatridgeInfo5().getRest())
                    +" " + totalInfos.get(i).getCatridgeInfo6().getName() + " : " + whatString(totalInfos.get(i).getCatridgeInfo6().getRest());

            ((CustomViewHolder)viewHolder).infoview.setText(string);

            // 클릭 시 작동하도록 하기
            if(isWork){
                viewHolder.itemView.setEnabled(false);
                // 디퓨저 작동 시에는 클릭 불가하도록
            }else{
                viewHolder.itemView.setEnabled(true);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onClick(View v) {

                        // 저장된 향 정보랑 현재 향 정보랑 다를 경우 못하도록

                        if( compareWith(totalInfos.get(i)) ){

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String temp = "2/2/2/2/2/2";

                            editor.putString("before", temp);
                            editor.commit(); // 이전에 켰었던 정보 저장

                            Bundle bundle = new Bundle();
                            Fragment turnon2fragment = new Turnon2Fragment();
                            bundle.putSerializable("total", totalInfos.get(i));
                            turnon2fragment.setArguments(bundle);
                            ((MainActivity)getActivity()).replaceFragment(turnon2fragment);
                        }else {
                            LayoutInflater inflater = getLayoutInflater();
                            View toastDesign = inflater.inflate(R.layout.toast_design, (ViewGroup) getView().findViewById(R.id.toast_design_root));
                            TextView textView = toastDesign.findViewById(R.id.toast_design_textview); // 토스트 꾸미기 위함
                            textView.setTextColor(R.color.colorPrimaryDark);

                            textView.setText("현재 가진 향 정보와 맞지 않습니다.");
                            Toast toast = new Toast(getContext());
                            toast.setGravity(Gravity.BOTTOM, 0, 30);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(toastDesign);
                            toast.show();
                        }

                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return totalInfos.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView nameview;
            public TextView infoview;

            public CustomViewHolder(View view) {
                super(view);
                nameview = (TextView)view.findViewById(R.id.item_scentinfo_name);
                infoview = (TextView)view.findViewById(R.id.item_scentinfo_info);
            }
        }
    }

    // 현재 향 정보를 가져올 함수
    public void init(){

        final ArrayList<String> datas = new ArrayList<String>();
        final ArrayList<Integer> rest = new ArrayList<Integer>();

        // 향 정보 받아오기
        FirebaseDatabase.getInstance().getReference("catridge").child(key).addValueEventListener(new ValueEventListener() {

            @SuppressLint("Range")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                datas.clear();
                rest.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    CatridgeInfo catridgeInfo = snapshot.getValue(CatridgeInfo.class);
                    datas.add(catridgeInfo.getName());
                    rest.add(catridgeInfo.getRest());
                } // 향 정보 받아오기

                nowInfo.setCatridgeInfo1(new CatridgeInfo(datas.get(0), rest.get(0)));
                nowInfo.setCatridgeInfo2(new CatridgeInfo(datas.get(1), rest.get(1)));
                nowInfo.setCatridgeInfo3(new CatridgeInfo(datas.get(2), rest.get(2)));
                nowInfo.setCatridgeInfo4(new CatridgeInfo(datas.get(3), rest.get(3)));
                nowInfo.setCatridgeInfo5(new CatridgeInfo(datas.get(4), rest.get(4)));
                nowInfo.setCatridgeInfo6(new CatridgeInfo(datas.get(5), rest.get(5)));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // 현재 향 정보가 나의 향 정보랑 같은지 비교하는 함수
    public Boolean compareWith(TotalInfo totalInfo){

        if(totalInfo.getCatridgeInfo1().getName().equals( nowInfo.getCatridgeInfo1().getName()) &&
                totalInfo.getCatridgeInfo2().getName().equals( nowInfo.getCatridgeInfo2().getName())&&
                totalInfo.getCatridgeInfo3().getName().equals( nowInfo.getCatridgeInfo3().getName()) &&
                totalInfo.getCatridgeInfo4().getName().equals(nowInfo.getCatridgeInfo4().getName()) &&
                totalInfo.getCatridgeInfo5().getName().equals(nowInfo.getCatridgeInfo5().getName()) &&
                totalInfo.getCatridgeInfo6().getName().equals( nowInfo.getCatridgeInfo6().getName()) ){
            return true;
        }

       return false;
    }

    private String whatString(int rest) {
        String tmp = "";
        switch (rest){
            case 0:
                tmp = "0% ";
                break;
            case 1:
                tmp = "50% ";
                break;
            case 2:
                tmp = "100% ";
                break;
        }
        return tmp;
    }
}
