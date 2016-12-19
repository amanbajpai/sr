package com.ros.smartrocket.utils;

import android.util.Log;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class Version implements Comparable<Version> {

    /**
     * The major version number.
     */
    private final int major;

    /**
     * The minor version number.
     */
    private final int minor;

    /**
     * The patch version number.
     */
    private final int patch;

    private static final String SEPARATOR = ".";

    /**
     * Constructs a {@code NormalVersion} with the
     * major, minor and patch version numbers.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @param patch the patch version number
     * @throws IllegalArgumentException if one of the version numbers is a negative integer
     */
    Version(int major, int minor, int patch) {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException(
                    "Major, minor and patch versions MUST be non-negative integers."
            );
        }
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /**
     * Returns the major version number.
     *
     * @return the major version number
     */
    int getMajor() {
        return major;
    }

    /**
     * Returns the minor version number.
     *
     * @return the minor version number
     */
    int getMinor() {
        return minor;
    }

    /**
     * Returns the patch version number.
     *
     * @return the patch version number
     */
    int getPatch() {
        return patch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Version other) {
        int result = major - other.major;
        if (result == 0) {
            result = minor - other.minor;
            if (result == 0) {
                result = patch - other.patch;
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Version)) {
            return false;
        }
        return compareTo((Version) other) == 0;
    }

    public Version(String version) {
        int major = 0;
        int minor = 0;
        int patch = 0;

        try {
            StringTokenizer st = new StringTokenizer(version, SEPARATOR, true);
            major = Integer.parseInt(st.nextToken());

            if (st.hasMoreTokens()) {
                st.nextToken(); // consume delimiter
                minor = Integer.parseInt(st.nextToken());

                if (st.hasMoreTokens()) {
                    st.nextToken(); // consume delimiter
                    patch = Integer.parseInt(st.nextToken());
                }
            }
        } catch (NoSuchElementException e) {
            Log.e("NoSuchElementException", "Use def values", e);
        }

        this.major = major;
        this.minor = minor;
        this.patch = patch;
        validate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + major;
        hash = 31 * hash + minor;
        hash = 31 * hash + patch;
        return hash;
    }

    /**
     * Returns the string representation of this normal version.
     * <p>
     * A normal version number MUST take the form X.Y.Z where X, Y, and Z are
     * non-negative integers. X is the major version, Y is the minor version,
     * and Z is the patch version. (SemVer p.2)
     *
     * @return the string representation of this normal version
     */
    @Override
    public String toString() {
        return String.format("%d.%d.%d", major, minor, patch);
    }


    private void validate() {
        if (major < 0) {
            throw new IllegalArgumentException("negative major"); //$NON-NLS-1$

        }
        if (minor < 0) {
            throw new IllegalArgumentException("negative minor"); //$NON-NLS-1$

        }
        if (patch < 0) {
            throw new IllegalArgumentException("negative patch"); //$NON-NLS-1$

        }
    }
}