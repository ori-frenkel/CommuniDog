package communi.dog.aplicatiion;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UnapprovedUserViewHolder extends RecyclerView.ViewHolder {
    TextView emailTextView;
    ImageView approveBtn;
    ImageView disapproveBtn;
    View view;

    public UnapprovedUserViewHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
        emailTextView = itemView.findViewById(R.id.emailToApproveTextView);
        approveBtn = itemView.findViewById(R.id.approveUserBtn);
        disapproveBtn = itemView.findViewById(R.id.disapproveUserBtn);
    }
}
