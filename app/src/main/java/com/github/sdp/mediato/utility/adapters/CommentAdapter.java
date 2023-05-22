package com.github.sdp.mediato.utility.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.github.sdp.mediato.R;
import com.github.sdp.mediato.model.Comment;
import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
  private List<Comment> comments = new ArrayList<>();

  public class CommentViewHolder extends RecyclerView.ViewHolder {
    TextView username, comment;

    public CommentViewHolder(@NonNull View itemView) {
      super(itemView);
      username = itemView.findViewById(R.id.username);
      comment = itemView.findViewById(R.id.comment);
    }
  }

  @NonNull
  @Override
  public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment, parent, false);
    return new CommentViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
    Comment currentComment = comments.get(position);
    holder.username.setText(currentComment.getRefUsername());
    holder.comment.setText(currentComment.getText());
  }

  @Override
  public int getItemCount() {
    return comments.size();
  }

  public void addComment(Comment comment) {
    comments.add(comment);
    notifyItemInserted(comments.size() - 1);
  }

}

