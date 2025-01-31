/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2023 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.ext.athena;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.ext.athena.model.AthenaConstants;
import org.jkiss.dbeaver.ext.athena.model.AthenaDataSource;
import org.jkiss.dbeaver.ext.athena.model.AthenaMetaModel;
import org.jkiss.dbeaver.ext.generic.GenericDataSourceProvider;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.DBPDataSourceContainer;
import org.jkiss.dbeaver.model.DBPInformationProvider;
import org.jkiss.dbeaver.model.DBPObject;
import org.jkiss.dbeaver.model.app.DBPPlatform;
import org.jkiss.dbeaver.model.connection.DBPConnectionConfiguration;
import org.jkiss.dbeaver.model.connection.DBPDriver;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.utils.CommonUtils;

public class AthenaDataSourceProvider extends GenericDataSourceProvider implements DBPInformationProvider {

    private static final Log log = Log.getLog(AthenaDataSourceProvider.class);

    public AthenaDataSourceProvider()
    {
    }

    @Override
    public void init(@NotNull DBPPlatform platform) {

    }

    @NotNull
    @Override
    public DBPDataSource openDataSource(
        @NotNull DBRProgressMonitor monitor,
        @NotNull DBPDataSourceContainer container)
        throws DBException
    {
        return new AthenaDataSource(monitor, container, new AthenaMetaModel());
    }

    @Override
    public String getConnectionURL(DBPDriver driver, DBPConnectionConfiguration connectionInfo) {
        //jdbc:awsathena://AwsRegion=us-east-1;
        String urlTemplate = driver.getSampleURL();
        String regionName = connectionInfo.getServerName();
        if (regionName == null) {
            regionName = connectionInfo.getProviderProperty(AthenaConstants.DRIVER_PROP_REGION);
        }
        if (CommonUtils.isEmpty(urlTemplate) || !urlTemplate.startsWith(AthenaConstants.JDBC_URL_PREFIX)) {
            return AthenaConstants.JDBC_URL_PREFIX + AthenaConstants.DRIVER_PROP_REGION + "=" + regionName + ";";
        }
        urlTemplate = urlTemplate
            .replace("{region}", regionName)
            .replace("{server}", regionName)
            .replace("=region;", "=" + regionName + ";"); // Left for backward compatibility
        return urlTemplate;
    }

    @Nullable
    @Override
    public String getObjectInformation(@NotNull DBPObject object, @NotNull String infoType) {
        if (object instanceof DBPDataSourceContainer && infoType.equals(INFO_TARGET_ADDRESS)) {
            return ((DBPDataSourceContainer) object).getConnectionConfiguration().getServerName();
        }
        return null;
    }

}
