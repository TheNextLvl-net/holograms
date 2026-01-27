package net.thenextlvl.hologram.action;

import net.kyori.adventure.key.KeyPattern;

record SimpleActionType<T>(@KeyPattern.Value String name, Class<T> type, Action<T> action) implements ActionType<T> {
}
