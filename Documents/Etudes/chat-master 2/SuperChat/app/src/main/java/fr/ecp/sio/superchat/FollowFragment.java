package fr.ecp.sio.superchat;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import fr.ecp.sio.superchat.loaders.FollowersLoader;
import fr.ecp.sio.superchat.loaders.FollowingLoader;
import fr.ecp.sio.superchat.model.User;
/**
 * Created by Selim on 19/01/15.
 */
public class FollowFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<User>> {

private static final int LOADER_USERS = 1000;
private static final int REQUEST_LOGIN_FOR_POST = 1;
private static final String ARG_USER = "user";
private static final String ARG_FOLLOW = "followOrFollowed";

private User mUser;
private Boolean followers;



private FollowAdapter mListAdapter;
private boolean mIsMasterDetailsMode;

    OnUnfollowingListener mCallback;

    public FollowAdapter getmListAdapter() {
        return mListAdapter;
    }

    // Container Activity must implement this interface
    public interface OnUnfollowingListener {
        public void onUserSelected();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnUnfollowingListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public static Bundle newFollowerArguments(User user) {
        Bundle args = new Bundle();

        args.putParcelable(ARG_USER, user);
        args.putBoolean(ARG_FOLLOW, true);


        return args;
    }

    public static Bundle newFollowingArguments(User user) {
        Bundle args = new Bundle();

        args.putParcelable(ARG_USER, user);
        args.putBoolean(ARG_FOLLOW, false);

        return args;
    }





    @Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mUser = getArguments().getParcelable(ARG_USER);
        followers = getArguments().getBoolean(ARG_FOLLOW);

        return inflater.inflate(R.layout.follow_fragment, container, false);
}

@Override
public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListAdapter = new FollowAdapter();
        setListAdapter(mListAdapter);
        view.findViewById(R.id.post).setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
        post();
        }
        });
        }

@Override
public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*      mIsMasterDetailsMode = getActivity().findViewById(R.id.tweets_content) != null;
        if (mIsMasterDetailsMode) {
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        }
        */
    getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                       int position, long id) {


            User user = mListAdapter.getItem(position);
            unfollow(user.getHandle());

            return true;
        }
    });
        }

@Override
public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(LOADER_USERS, null, this);
        }

@Override
public Loader<List<User>> onCreateLoader(int id, Bundle args) {
    if (followers)
        return new FollowersLoader(getActivity(),mUser.getHandle());
    else
        return new FollowingLoader(getActivity(),mUser.getHandle());
}

@Override
public void onLoadFinished(Loader<List<User>> loader, List<User> users) {
        mListAdapter.setUsers(users);
        }

@Override
public void onLoaderReset(Loader<List<User>> loader) { }

@Override
public void onListItemClick(ListView l, View v, int position, long id) {
        User user = mListAdapter.getItem(position);
       if (mIsMasterDetailsMode) {
        Fragment tweetsFragment = new TweetsFragment();
        tweetsFragment.setArguments(TweetsFragment.newArguments(user));
        getFragmentManager()
        .beginTransaction()
        .replace(R.id.tweets_content, tweetsFragment)
        .commit();
        } else {

        Intent intent = new Intent(getActivity(), TweetsActivity.class);
        intent.putExtras(TweetsFragment.newArguments(user));
        startActivity(intent);
      }
}



private void post() {
        if (AccountManager.isConnected(getActivity())) {
        startActivity(new Intent(getActivity(), PostActivity.class));
        } else {
        LoginFragment fragment = new LoginFragment();
        fragment.setTargetFragment(this, REQUEST_LOGIN_FOR_POST);
        fragment.show(getFragmentManager(), "login_dialog");
        }
        }

@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN_FOR_POST && resultCode == PostActivity.RESULT_OK) {
        post();
        }
        }




    public void unfollow(final String userToUnFollow) {


        new AsyncTask<String, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    String userToUnfollow = params[0];
                    String handle = AccountManager.getUserHandle(getActivity());
                    String token = AccountManager.getUserToken(getActivity());
                    new ApiClient().unfollow(handle, token, userToUnfollow);
                    return true;
                } catch (IOException e) {
                    Log.e(FollowActivity.class.getName(), "Post failed", e);
                    return false;
                }
            }
            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {

                    mListAdapter.notifyDataSetChanged();
                    getLoaderManager().restartLoader(0, null, FollowFragment.this);

                    Toast.makeText(getActivity(), userToUnFollow+" is unfollowed", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), R.string.post_error, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(userToUnFollow);}



}

