package name.tjsingleton.todoapp;

import com.google.common.collect.ImmutableList;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

@Repository
@ParametersAreNonnullByDefault
@ThreadSafe
public class TodoRepository {
    private static ConcurrentMap<UUID, Todo> storage = new ConcurrentHashMap<>();

    public Optional<Todo> save(Todo aTodo) {
        return Optional.ofNullable(storage.put(aTodo.getId(), aTodo));
    }

    public Collection<Todo> all(IndexFilter filter) {
        List<UUID> ids = filter.getIds();
        if (!ids.isEmpty()) {
            List<Todo> filtered = new ArrayList<>(ids.size());
            for (UUID id : ids) {
                Todo todo = storage.get(id);
                if (todo != null) {
                    filtered.add(todo);
                }
            }
            return filtered;
        } else {
            return ImmutableList.copyOf(storage.values());
        }
    }

    public void clear() {
        storage.clear();
    }

    public Optional<Todo> findOne(UUID uuid) {
        return Optional.ofNullable(storage.get(uuid));
    }

    public Optional<Todo> remove(UUID uuid) {
        return Optional.ofNullable(storage.remove(uuid));
    }
}
