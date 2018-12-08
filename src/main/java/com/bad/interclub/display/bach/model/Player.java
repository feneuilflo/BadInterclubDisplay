package com.bad.interclub.display.bach.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Player {

    private final String surname;
    private final String firstname;
    private final String category; // junior, senior...
    private final int licence;

    private final String single_category;
    private final int single_cpph;

    private final String double_category;
    private final int double_cpph;

    private final String mixed_double_category;
    private final int mixed_double_cpph;

    public Player(String surname, String firstname, String category, int licence, String single_category, int single_cpph, String double_category, int double_cpph, String mixed_double_category, int mixed_double_cpph) {
        this.surname = surname;
        this.firstname = firstname;
        this.category = category;
        this.licence = licence;
        this.single_category = single_category;
        this.single_cpph = single_cpph;
        this.double_category = double_category;
        this.double_cpph = double_cpph;
        this.mixed_double_category = mixed_double_category;
        this.mixed_double_cpph = mixed_double_cpph;
    }

    public static PlayerBuilder newBuilder() {
        return new PlayerBuilder();
    }

    public String getSurname() {
        return surname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getCategory() {
        return category;
    }

    public int getLicence() {
        return licence;
    }

    public String getSingleCategory() {
        return single_category;
    }

    public int getSingleCPPH() {
        return single_cpph;
    }

    public String getDoubleCategory() {
        return double_category;
    }

    public int getDoubleCPPH() {
        return double_cpph;
    }

    public String getMixedDoubleCategory() {
        return mixed_double_category;
    }

    public int getMixedDoubleCPPH() {
        return mixed_double_cpph;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("surname", surname)
                .append("firstname", firstname)
                .append("category", category)
                .append("licence", licence)
                .append("single_category", single_category)
                .append("single_cpph", single_cpph)
                .append("double_category", double_category)
                .append("double_cpph", double_cpph)
                .append("mixed_double_category", mixed_double_category)
                .append("mixed_double_cpph", mixed_double_cpph)
                .toString();
    }

    public static final class PlayerBuilder {
        private String surname;
        private String firstname;
        private String category;
        private int licence;
        private String single_category;
        private int single_cpph;
        private String double_category;
        private int double_cpph;
        private String mixed_double_category;
        private int mixed_double_cpph;

        PlayerBuilder() {
        }

        public PlayerBuilder withSurname(String surname) {
            this.surname = surname;
            return this;
        }

        public PlayerBuilder withFirstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public PlayerBuilder withCategory(String category) {
            this.category = category;
            return this;
        }

        public PlayerBuilder withLicence(int licence) {
            this.licence = licence;
            return this;
        }

        public PlayerBuilder withSingleCategory(String single_category) {
            this.single_category = single_category;
            return this;
        }

        public PlayerBuilder withSingleCPPH(int single_cpph) {
            this.single_cpph = single_cpph;
            return this;
        }

        public PlayerBuilder withDoubleCategory(String double_category) {
            this.double_category = double_category;
            return this;
        }

        public PlayerBuilder withDoubleCPPH(int double_cpph) {
            this.double_cpph = double_cpph;
            return this;
        }

        public PlayerBuilder withMixedDoubleCategory(String mixed_double_category) {
            this.mixed_double_category = mixed_double_category;
            return this;
        }

        public PlayerBuilder withMixedDoubleCPPH(int mixed_double_cpph) {
            this.mixed_double_cpph = mixed_double_cpph;
            return this;
        }

        public Player build() {
            return new Player(surname, firstname, category, licence, single_category, single_cpph, double_category, double_cpph, mixed_double_category, mixed_double_cpph);
        }
    }
}
