package com.yudachi.login.service;

import com.yudachi.model.user.pojos.ApUser;

/**
 * 对称加密算法 DES AES
 * 散列算法 MD5 扩展加盐 salt
 */
public interface ValidateService {

    /**
     * DES验证
     * @param user
     * @param db
     * @return
     */
    boolean validDES(ApUser user, ApUser db);

    /**
     * MD5验证
     * @param user
     * @param db
     * @return
     */
    boolean validMD5(ApUser user, ApUser db);

    /**
     * MD5加盐验证
     * @param user
     * @param db
     * @return
     */
    boolean validMD5WithSalt(ApUser user, ApUser db);
}
