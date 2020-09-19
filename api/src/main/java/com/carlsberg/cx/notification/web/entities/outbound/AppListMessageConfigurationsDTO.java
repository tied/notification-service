package com.carlsberg.cx.notification.web.entities.outbound;

import com.carlsberg.cx.notification.web.entities.inbound.AppMessageConfigurationDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** SMS/Email Provider Apps configuration */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppListMessageConfigurationsDTO {

  /** List of Apps configurations */
  private List<AppMessageConfigurationDTO> appsConfigs;

}
