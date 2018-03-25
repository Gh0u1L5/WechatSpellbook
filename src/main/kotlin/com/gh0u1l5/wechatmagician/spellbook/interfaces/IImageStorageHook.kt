package com.gh0u1l5.wechatmagician.spellbook.interfaces

interface IImageStorageHook {

    /**
     * Called when an ImgInfoStorage object has been created.
     *
     * @param storage the ImgInfoStorage object created by a constructor.
     */
    fun onImageStorageCreated(storage: Any) { }

    /**
     * Called when an ImgInfoStorage is loading a new image information.
     *
     * @param imageId the internal ID of the image.
     * @param prefix
     * @param suffix
     * @return to bypass the original method, return `true`, otherwise return `false`.
     */
    fun onImageStorageLoading(imageId: String?, prefix: String?, suffix: String?) = false

    /**
     * Called when an ImgInfoStorage has loaded a new image information.
     *
     * @param imageId the internal ID of the image.
     * @param prefix
     * @param suffix
     */
    fun onImageStorageLoaded(imageId: String?, prefix: String?, suffix: String?) { }
}