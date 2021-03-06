package com.raizlabs.android.dbflow.sql.language;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.Query;
import com.raizlabs.android.dbflow.sql.QueryBuilder;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;

/**
 * Description: Describes an easy way to create a SQLite TRIGGER
 */
public class Trigger implements Query {

    /**
     * Specifies that we should do this TRIGGER before some event
     */
    public static final String BEFORE = "BEFORE";

    /**
     * Specifies that we should do this TRIGGER after some event
     */
    public static final String AFTER = "AFTER";

    /**
     * Specifies that we should do this TRIGGER instead of the specified events
     */
    public static final String INSTEAD_OF = "INSTEAD OF";

    /**
     * The name in the DB
     */
    final String triggerName;

    /**
     * If it's {@link #BEFORE}, {@link #AFTER}, or {@link #INSTEAD_OF}
     */
    String beforeOrAfter;

    boolean temporary;

    /**
     * @param triggerName The name of the trigger to use.
     * @return A new trigger.
     */
    @NonNull
    public static Trigger create(@NonNull String triggerName) {
        return new Trigger(triggerName);
    }

    /**
     * Creates a trigger with the specified trigger name. You need to complete
     * the trigger using
     *
     * @param triggerName What we should call this trigger
     */
    private Trigger(@NonNull String triggerName) {
        this.triggerName = triggerName;
    }

    /**
     * Sets the trigger as temporary.
     */
    @NonNull
    public Trigger temporary() {
        this.temporary = true;
        return this;
    }

    /**
     * Specifies AFTER eventName
     */
    @NonNull
    public Trigger after() {
        beforeOrAfter = AFTER;
        return this;
    }

    /**
     * Specifies BEFORE eventName
     */
    @NonNull
    public Trigger before() {
        beforeOrAfter = BEFORE;
        return this;
    }

    /**
     * Specifies INSTEAD OF eventName
     */
    @NonNull
    public Trigger insteadOf() {
        beforeOrAfter = INSTEAD_OF;
        return this;
    }

    /**
     * Starts a DELETE ON command
     *
     * @param onTable The table ON
     */
    @NonNull
    public <TModel> TriggerMethod<TModel> deleteOn(@NonNull Class<TModel> onTable) {
        return new TriggerMethod<>(this, TriggerMethod.DELETE, onTable);
    }

    /**
     * Starts a INSERT ON command
     *
     * @param onTable The table ON
     */
    @NonNull
    public <TModel> TriggerMethod<TModel> insertOn(@NonNull Class<TModel> onTable) {
        return new TriggerMethod<>(this, TriggerMethod.INSERT, onTable);
    }

    /**
     * Starts an UPDATE ON command
     *
     * @param onTable    The table ON
     * @param properties if empty, will not execute an OF command. If you specify columns,
     *                   the UPDATE OF column1, column2,... will be used.
     */
    @NonNull
    public <TModel> TriggerMethod<TModel> updateOn(@NonNull Class<TModel> onTable, IProperty... properties) {
        return new TriggerMethod<>(this, TriggerMethod.UPDATE, onTable, properties);
    }

    /**
     * @return The name of this TRIGGER
     */
    @NonNull
    public String getName() {
        return triggerName;
    }

    @Override
    public String getQuery() {
        QueryBuilder queryBuilder = new QueryBuilder("CREATE ");
        if (temporary) {
            queryBuilder.append("TEMP ");
        }
        queryBuilder.append("TRIGGER IF NOT EXISTS ")
            .appendQuotedIfNeeded(triggerName).appendSpace()
            .appendOptional(beforeOrAfter + " ");

        return queryBuilder.getQuery();
    }
}
