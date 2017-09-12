package com.bing.lan.client;

import android.view.View;
import android.widget.TextView;

/**
 * Created by 蓝兵 on 2017/9/12.
 */

class MessageListAdapter extends BaseRecyclerAdapter<Message> {

    @Override
    public int getItemLayoutResId(int viewType) {
        if (viewType == ITEM_TYPE_NORMAL) {
            return R.layout.item_client;
        }
        return R.layout.item_server;
    }

    @Override
    public int getItemViewType(int position) {
        Message tableTitleBean = data.get(position);
        return tableTitleBean.getType();
    }

    @Override
    public BaseViewHolder createViewHolder(View itemView, int type) {
        if (type == ITEM_TYPE_NORMAL) {
            return new ClientHolder(itemView);
        }
        return new ServerHolder(itemView);
    }

    class ClientHolder extends BaseViewHolder {

        private final TextView tv_client_msg;

        public ClientHolder(View itemView) {
            super(itemView);
            tv_client_msg = itemView.findViewById(R.id.tv_client_msg);
        }

        @Override
        public void fillData(Message data, int position) {
            tv_client_msg.setText(data.getMsg());
        }
    }

    class ServerHolder extends BaseViewHolder {

        private final TextView tv_server_msg;

        public ServerHolder(View itemView) {
            super(itemView);

            tv_server_msg = itemView.findViewById(R.id.tv_server_msg);
        }

        @Override
        public void fillData(Message data, int position) {
            tv_server_msg.setText(data.getMsg());
        }
    }
}
