package org.springframework.security.web.access.repo;

import com.dianrong.common.uniauth.common.client.enums.AuthenticationType;
import org.springframework.security.web.context.SecurityContextRepository;

public interface UniauthSecurityContextRepository extends SecurityContextRepository {

  /**
   * 判断是否支持指定的验证类型.
   */
  boolean support(AuthenticationType authenticationType);
}
