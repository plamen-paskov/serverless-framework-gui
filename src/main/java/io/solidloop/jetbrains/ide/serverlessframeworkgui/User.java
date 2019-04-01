package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
@State(name = "User", storages = @Storage("serverless-framework-gui/user.xml"))
public class User implements PersistentStateComponent<User> {
    private String userId;
    private boolean tcAccepted = false;

    @Nullable
    @Override
    public User getState() {
        return this;
    }

    @Override
    public void loadState(final User state) {
        setUserId(state.getUserId());
        setTcAccepted(state.isTcAccepted());
    }
}
