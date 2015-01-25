package fr.ecp.sio.superchat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;


/**
 * Created by mac on 19/01/15.
 */
public class FollowActivity extends ActionBarActivity implements FollowFragment.OnUnfollowingListener{
    private static final String ARG_USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.follow_fragment);

        if (savedInstanceState == null) {
            FollowFragment followFragment = new FollowFragment();
            followFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.follow_frag, followFragment)
                    .commit();
        }
    }

    @Override
    public void onUserSelected() {
        FollowFragment followFrag = (FollowFragment)getSupportFragmentManager().findFragmentById(R.id.follow_frag);

        followFrag.getmListAdapter().notifyDataSetChanged();
        followFrag.getLoaderManager().restartLoader(0, null, followFrag);

//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.follow_frag, followFrag).commit();
//
//        for (Fragment frag : getSupportFragmentManager().getFragments()){
//            if (frag.class.isInstance(FollowFragment.class)){
//                    frag.getAda
//            }
//        }



        //getLoaderManager().restartLoader();

        Log.i("ZEB ZEBI", "ZEB ZEB ZEBI");

//        if (followFrag!=null){
//
//        }
    }
}