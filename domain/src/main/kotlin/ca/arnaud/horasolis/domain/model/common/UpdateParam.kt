package ca.arnaud.horasolis.domain.model.common

// out Data is used to make UpdateParam covariant
sealed interface UpdateParam<out Data> {

    data object Unchanged : UpdateParam<Nothing>

    data class Update<Data>(val data: Data) : UpdateParam<Data>

    companion object {

        fun <Data> updateIfNotNull(data: Data?): UpdateParam<Data> {
            return data?.let { Update(it) } ?: Unchanged
        }

        /**
         * create an [UpdateParam] based on the original and updated data.
         *
         * @param originalData the original data to compare [updatedData] with.
         * @param updatedData the updated data with the potential new changes.
         * @return [UpdateParam.Unchanged] if the original and updated data are the same,
         *  or [UpdateParam.Update] if the original and updated data are different.
         */
        fun <Data> of(originalData: Data, updatedData: Data): UpdateParam<Data> {
            return if (originalData != updatedData) {
                Update(updatedData)
            } else {
                Unchanged
            }
        }
    }

    /**
     * Returns data if it is [UpdateParam.Update], otherwise returns null.
     *
     * IMPORTANT - Should only be used when reading the data, should not be used for building request update.
     *  For building request update, use `getUpdateDataOrDefault` instead.
     *
     * @return the data if it is [UpdateParam.Update], otherwise returns null.
     */
    fun <Data> UpdateParam<Data>.getUpdateOrNull(): Data? {
        return when (this) {
            is Update -> data
            is Unchanged -> null
        }
    }

    /**
     * Returns data if it is [UpdateParam.Update], otherwise returns [defaultValue].
     * To call when building a request model send changed value (null or not null), or use previous value if unchanged.
     *
     * @param defaultValue the value to return if the data is [UpdateParam.Unchanged].
     * @return the data if it is [UpdateParam.Update], otherwise returns [defaultValue].
     */
    fun <Data> UpdateParam<Data>.getUpdateDataOrDefault(defaultValue: Data): Data {
        return when (this) {
            is Update -> data
            is Unchanged -> defaultValue
        }
    }

    /**
     * map data if it is [UpdateParam.Update], otherwise returns [defaultValue].
     * To use instead of [getUpdateDataOrDefault] when data needs to be transformed.
     * For example when mapping domain model to remote model.
     *
     * @param defaultValue the value to return if the data is [UpdateParam.Unchanged].
     * @param transform the function to transform the data if it is [UpdateParam.Update].
     * @return the data if it is [UpdateParam.Update] after transformation, otherwise returns [defaultValue].
     */
    fun <Data, NewData> UpdateParam<Data>.mapUpdateDataOrDefault(
        defaultValue: NewData,
        transform: (Data) -> NewData,
    ): NewData {
        return when (this) {
            is Update -> transform(data)
            is Unchanged -> defaultValue
        }
    }
}