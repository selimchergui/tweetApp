package fr.ecp.sio.superchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import fr.ecp.sio.superchat.model.User;

/**
 * Created by Michaël on 05/12/2014.
 */
public class TweetsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tweets_activity);

        if (savedInstanceState == null) {
            Fragment tweetsFragment = new TweetsFragment();
            tweetsFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content, tweetsFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_follow, menu);

        User user = getIntent().getExtras().getParcelable("user");


        menu.getItem(0).setTitle(user.getHandle() + "'s followers");
        menu.getItem(1).setTitle("followed by "+user.getHandle());


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        User user = getIntent().getExtras().getParcelable("user");


        if (id == R.id.followers_view) {
            Intent intent = new Intent(this, FollowActivity.class);
            intent.putExtras(FollowFragment.newFollowerArguments(user));
            startActivity(intent);

            return true;
        }else if(id == R.id.followings_view){
            Intent intent = new Intent(this, FollowActivity.class);
            intent.putExtras(FollowFragment.newFollowingArguments(user));
            startActivity(intent);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
