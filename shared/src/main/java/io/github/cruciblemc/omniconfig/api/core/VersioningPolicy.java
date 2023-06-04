package io.github.cruciblemc.omniconfig.api.core;

import io.github.cruciblemc.omniconfig.api.annotation.AnnotationConfig;
import io.github.cruciblemc.omniconfig.api.lib.Version;

/**
 * A widely known issue of most config implementations is that its rather difficult for
 * developers to update default values in their config; while these values can be
 * changed in the code, they will persist in existing physical files for users that
 * had previous versions of the mod installed, even if users themselves never actually
 * touched upon these files. This sort of "stuck" values often become an issue, for instance
 * - when exposing vast amount of options that give control over mod's gameplay balance,
 * without that balance being in its final state yet.<br><br>
 * <p>
 * But fear not, for {@link VersioningPolicy} is a long-awaited solution to that problem.
 * Versioning policies allow omniconfig implementers to choose among four distinct strategies
 * for updating default values in already generated config files. Config files from different
 * versions of the mod are distinguished by their own internal version, which can be defined when
 * setting up the builder, or among parameters of {@link AnnotationConfig} annotation in case of
 * annotation config class.<br><br>
 * <p>
 * The way {@link #RESPECTFUL} and {@link #NOBLE} policies work is achieved by storing an archived
 * local copy of every omniconfig file, which is guaranteed to not be modified by user, and hereby
 * allows to retain information about default values of properties for old file, thus allowing to
 * compare these values to what they are in new version. If you use either of this two policies,
 * it is strongly adviced that you increment your config's {@link Version} anytime you release a
 * mod version that changes something about default state of that config. This is neccessary to
 * ensure that your updating strategy will correctly apply at all times; otherwise some of the
 * values may end up having their defaults stuck from files generated by old mod versions,
 * effectively becoming treated as user-modified.<br><br>
 * <p>
 * It is also worth noting that versioning policy of your choice will apply indentically both in
 * case physical file has higher version than current, and in case that version is lower.
 *
 * @author Aizistral
 */

public enum VersioningPolicy {

    /**
     * This policy enforces full reset of config file if version does not match.
     * Contents of the existing file will be entirely disregarded when loading,
     * therefore all values will fall back to their defaults when saving, and any
     * unneccessary data will be cleaned.
     */
    AGGRESSIVE,

    /**
     * If config version was changed, automatically resets values of every property in old
     * file that had its default value altered in new version of the config.<br>
     * Properties that didn't have their default value altered between versions remain untouched,
     * but those that have will be reset regardless of whether or not user have made changes
     * to them.
     */
    RESPECTFUL,

    /**
     * Same as {@link #RESPECTFUL}, but also takes into account whether or not user have altered
     * properties manually. If user have modified some of the properties in the old file, these
     * modified properties will always persist as they are, otherwise does everything the same
     * as previous policy.
     */
    NOBLE,

    /**
     * With this policy (used by default) config implementation will not take any
     * action if config versions do not match. It will simply update config version
     * to defined one upon saving the file, and leave anything else as is.
     */
    DISMISSIVE
}