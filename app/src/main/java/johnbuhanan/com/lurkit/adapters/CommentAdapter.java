package johnbuhanan.com.lurkit.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import johnbuhanan.com.lurkit.R;
import johnbuhanan.com.lurkit.model.Comment;

public class CommentAdapter extends ArrayAdapter<Comment> {

    ArrayList<Comment> mComments = new ArrayList<>();

    public CommentAdapter(Context context) {
        super(context, 0);
    }

    public void addComments(ArrayList<Comment> comments) {
        this.mComments.addAll(comments);
        notifyDataSetChanged();
//        notifyItemRangeChanged(0, this.redditPosts.size());
    }

    @Override
    public int getCount() {
        return mComments.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentViewHolder holder;

        Comment comment = mComments.get(position);

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_row, parent, false);
            holder = new CommentViewHolder();

            holder.comment_indicator_space = (LinearLayout) convertView.findViewById(R.id.comment_indicator_space);
            holder.comment_indicator_color = (LinearLayout) convertView.findViewById(R.id.comment_indicator_color);
            holder.comment_author = (TextView) convertView.findViewById(R.id.comment_author);
            holder.comment_score = (TextView) convertView.findViewById(R.id.comment_score);
            holder.comment_body = (TextView) convertView.findViewById(R.id.comment_body);

            convertView.setTag(holder);
        } else {

            holder = (CommentViewHolder) convertView.getTag();
        }
        holder.comment_author.setText(comment.getComment_author());
        holder.comment_score.setText(comment.getComment_score() + "points");
        holder.comment_body.setText(Html.fromHtml(comment.getComment_body()));
        holder.comment_body.setMovementMethod(LinkMovementMethod.getInstance());

        //handle comment replies/children and set the appropriate indication colors
        if (mComments.get(position).getLevel() == 0) {
            holder.comment_indicator_space.setPadding(0, 0, 0, 0);
            holder.comment_indicator_color.setVisibility(View.GONE);
        } else if (mComments.get(position).getLevel() == 1) {
            holder.comment_indicator_space.setPadding(mComments.get(position).getLevel() * 8, 0, 0, 0);
            holder.comment_indicator_color.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_800));
            holder.comment_indicator_color.setVisibility(View.VISIBLE);
        } else if (mComments.get(position).getLevel() == 2) {
            holder.comment_indicator_space.setPadding(mComments.get(position).getLevel() * 8, 0, 0, 0);
            holder.comment_indicator_color.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple_800));
            holder.comment_indicator_color.setVisibility(View.VISIBLE);
        } else if (mComments.get(position).getLevel() == 3) {
            holder.comment_indicator_space.setPadding(mComments.get(position).getLevel() * 8, 0, 0, 0);
            holder.comment_indicator_color.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green_800));
            holder.comment_indicator_color.setVisibility(View.VISIBLE);
        } else if (mComments.get(position).getLevel() == 4) {
            holder.comment_indicator_space.setPadding(mComments.get(position).getLevel() * 8, 0, 0, 0);
            holder.comment_indicator_color.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.orange_800));
            holder.comment_indicator_color.setVisibility(View.VISIBLE);
        } else if (mComments.get(position).getLevel() == 5) {
            holder.comment_indicator_space.setPadding(mComments.get(position).getLevel() * 8, 0, 0, 0);
            holder.comment_indicator_color.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_800));
            holder.comment_indicator_color.setVisibility(View.VISIBLE);
        } else if (mComments.get(position).getLevel() == 6) {
            holder.comment_indicator_space.setPadding(mComments.get(position).getLevel() * 8, 0, 0, 0);
            holder.comment_indicator_color.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.yellow_800));
            holder.comment_indicator_color.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public static class CommentViewHolder {
        LinearLayout comment_indicator_space, comment_indicator_color;
        TextView comment_author, comment_score, comment_body;
    }
}