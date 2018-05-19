package org.yeb;

import com.badlogic.gdx.utils.Disposable;

import java.util.List;

public interface CollectingDisposable extends Disposable {

    List<Disposable> disposables();

    default <T extends Disposable> T register(T disposable) {
        disposables().add(disposable);
        return disposable;
    }

    @Override
    default void dispose() {
        disposables().forEach(Disposable::dispose);
    }
}
