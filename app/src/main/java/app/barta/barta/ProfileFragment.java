package app.barta.barta;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.Duration;
import org.threeten.bp.OffsetDateTime;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private TextView ageValue;
    private TextView karmaCount;
    private TextView postKarmaCount;
    private TextView postUpvoteCount;
    private TextView postDownvoteCount;
    private TextView commentKarmaCount;
    private TextView commentUpvoteCount;
    private TextView commentDownvoteCount;

    private final Handler handler = new Handler();
    private final Runnable fetcher = new Runnable() {
        @Override
        public void run() {
            fetch();
            handler.postDelayed(fetcher, 5000);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity)getContext()).getSupportActionBar().setTitle(R.string.title_profile);
        View fragmentView = inflater.inflate(R.layout.fragment_profile, null);
        ageValue = fragmentView.findViewById(R.id.ageValue);
        karmaCount = fragmentView.findViewById(R.id.karmaCount);
        postKarmaCount = fragmentView.findViewById(R.id.postKarmaCount);
        postUpvoteCount = fragmentView.findViewById(R.id.postUpvoteCount);
        postDownvoteCount = fragmentView.findViewById(R.id.postDownvoteCount);
        commentKarmaCount = fragmentView.findViewById(R.id.commentKarmaCount);
        commentUpvoteCount = fragmentView.findViewById(R.id.commentUpvoteCount);
        commentDownvoteCount = fragmentView.findViewById(R.id.commentDownvoteCount);
        handler.post(fetcher);
        return fragmentView;
    }

    @Override
    public void onStop() {
        handler.removeCallbacks(fetcher);
        super.onStop();
    }

    private void fetch() {
        String url = ((MainActivity) getContext()).getUserUrl() + "?projection=details";
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    ageValue.setText(calculateAge(jsonObject.getString("creationTime")) + " days");
                    karmaCount.setText(Integer.toString(jsonObject.getInt("karma")));
                    postKarmaCount.setText(Integer.toString(jsonObject.getInt("postKarma")));
                    postUpvoteCount.setText(Integer.toString(jsonObject.getInt("postUpvoteCount")));
                    postDownvoteCount.setText(Integer.toString(jsonObject.getInt("postDownvoteCount")));
                    commentKarmaCount.setText(Integer.toString(jsonObject.getInt("commentKarma")));
                    commentUpvoteCount.setText(Integer.toString(jsonObject.getInt("commentUpvoteCount")));
                    commentDownvoteCount.setText(Integer.toString(jsonObject.getInt("commentDownvoteCount")));
                } catch (JSONException e) {
                    Log.e(TAG, "Error while parsing JSON", e);
                }

                Log.d(TAG, "Finished fetching user information");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error while fetching posts", error);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    int calculateAge(String creationTimeString) {
        OffsetDateTime creationTime = OffsetDateTime.parse(creationTimeString);
        Duration duration = Duration.between(creationTime, OffsetDateTime.now());
        return (int) duration.toDays();
    }
}
