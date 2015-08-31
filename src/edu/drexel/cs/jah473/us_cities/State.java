package edu.drexel.cs.jah473.us_cities;

/**
 * The 50 US states, plus territories.
 * 
 * @author Justin Horvitz
 *
 */
public enum State {
    /**
     * Alaska.
     */
    AK,
    /**
     * Alabama.
     */
    AL,
    /**
     * Arkansas.
     */
    AR,
    /**
     * American Samoa.
     */
    AS,
    /**
     * Arizona.
     */
    AZ,
    /**
     * California.
     */
    CA,
    /**
     * Colorado.
     */
    CO,
    /**
     * Connecticut.
     */
    CT,
    /**
     * District of Columbia.
     */
    DC,
    /**
     * Delaware.
     */
    DE,
    /**
     * Florida.
     */
    FL,
    /**
     * Federated States of Micronesia.
     */
    FM,
    /**
     * Georgia.
     */
    GA,
    /**
     * Guam.
     */
    GU,
    /**
     * Hawaii.
     */
    HI,
    /**
     * Iowa.
     */
    IA,
    /**
     * Idaho.
     */
    ID,
    /**
     * Illinois.
     */
    IL,
    /**
     * Indiana.
     */
    IN,
    /**
     * Kansas.
     */
    KS,
    /**
     * Kentucky.
     */
    KY,
    /**
     * Louisiana.
     */
    LA,
    /**
     * Massachusetts.
     */
    MA,
    /**
     * Maryland.
     */
    MD,
    /**
     * Maine.
     */
    ME,
    /**
     * Marshall Islands.
     */
    MH,
    /**
     * Michigan.
     */
    MI,
    /**
     * Minnesota.
     */
    MN,
    /**
     * Missouri.
     */
    MO,
    /**
     * Northern Mariana Islands.
     */
    MP,
    /**
     * Mississippi.
     */
    MS,
    /**
     * Montana.
     */
    MT,
    /**
     * North Carolina.
     */
    NC,
    /**
     * North Dakota.
     */
    ND,
    /**
     * Nebraska.
     */
    NE,
    /**
     * New Hampshire.
     */
    NH,
    /**
     * New Jersey.
     */
    NJ,
    /**
     * New Mexico.
     */
    NM,
    /**
     * Nevada.
     */
    NV,
    /**
     * New York.
     */
    NY,
    /**
     * Ohio.
     */
    OH,
    /**
     * Oklahoma.
     */
    OK,
    /**
     * Oregon.
     */
    OR,
    /**
     * Pennsylvania.
     */
    PA,
    /**
     * Puerto Rico.
     */
    PR,
    /**
     * Palau.
     */
    PW,
    /**
     * Rhode Island.
     */
    RI,
    /**
     * South Carolina.
     */
    SC,
    /**
     * South Carolina.
     */
    SD,
    /**
     * Tennessee.
     */
    TN,
    /**
     * Texas.
     */
    TX,
    /**
     * Utah.
     */
    UT,
    /**
     * Virginia.
     */
    VA,
    /**
     * Virgin Islands.
     */
    VI,
    /**
     * Vermont.
     */
    VT,
    /**
     * Washington.
     */
    WA,
    /**
     * Wisconsin.
     */
    WI,
    /**
     * West Virginia.
     */
    WV,
    /**
     * Wyoming.
     */
    WY;

    /**
     * Returns this state's full name.
     * 
     * @return this state's full name.
     */
    public String fullName() {
        return USCities.fullStateName(this);
    }
}
