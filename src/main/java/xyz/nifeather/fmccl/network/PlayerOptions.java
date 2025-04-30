package xyz.nifeather.fmccl.network;

public class PlayerOptions<TPlatformPlayer>
{
    public PlayerOptions(TPlatformPlayer player)
    {
        this.player = player;
    }

    private final TPlatformPlayer player;

    private boolean clientSideSelfView;

    public boolean isClientSideSelfView()
    {
        return clientSideSelfView;
    }

    /**
     * 玩家客户端的接口版本，如果为-1则代表客户端尚未初始化
     */
    public int clientApiVersion = -1;

    public void setClientSideSelfView(boolean newVal)
    {
        clientSideSelfView = newVal;
    }

    /**
     * 是否在HUD上显示当前伪装?
     */
    public boolean displayDisguiseOnHUD = true;
}
