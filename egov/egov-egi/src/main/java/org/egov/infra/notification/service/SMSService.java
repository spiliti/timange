/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

package org.egov.infra.notification.service;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.egov.infra.notification.entity.NotificationPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static org.egov.infra.config.core.LocalizationSettings.countryCode;
import static org.egov.infra.config.core.LocalizationSettings.encoding;
import static org.egov.infra.notification.entity.NotificationPriority.MEDIUM;

@Service
public class SMSService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SMSService.class);
    private static final String SMS_PRIORITY_PARAM_VALUE = "sms.%s.priority.param.value";

    @Autowired
    private Environment environment;

    @Value("${sms.priority.enabled}")
    private boolean smsPriorityEnabled;

    @Value("${sms.priority.param.name}")
    private String smsPriorityParamName;

    @Value("${sms.provider.url}")
    private String smsProviderURL;

    @Value("${sms.sender.req.param.name}")
    private String senderReqParamName;

    @Value("${sms.sender}")
    private String sender;

    @Value("${sms.sender.username.req.param.name}")
    private String senderUserNameReqParamName;

    @Value("${sms.sender.username}")
    private String senderUserName;

    @Value("${sms.sender.password.req.param.name}")
    private String senderPasswordReqParamName;

    @Value("${sms.sender.password}")
    private String senderPassword;

    @Value("${sms.destination.mobile.req.param.name}")
    private String mobileNumberReqParamName;

    @Value("${sms.message.req.param.name}")
    private String messageReqParamName;

    @Value("${sms.type.req.param.name}")
    private String messageTypeParamName;

    @Value("${sms.message.secure.key}")
    private String messageSecureKey;

    @Value("#{'${sms.extra.req.params}'.split('&')}")
    private List<String> extraRequestParams;

    @Value("#{'${sms.error.codes}'.split(',')}")
    private List<String> smsErrorCodes;

    public boolean sendSMS(String mobileNumber, String message) {
        return sendSMS(mobileNumber, message, MEDIUM);
    }

    public boolean sendSMS(String mobileNumber, String message, NotificationPriority priority) {
        String encryptedPassword;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(smsProviderURL);
            List<NameValuePair> urlParameters = new ArrayList<>();
            encryptedPassword = MD5(senderPassword);
            String genratedhashKey = hashGenerator(senderUserName, sender, message, messageSecureKey);
            urlParameters.add(new BasicNameValuePair(senderUserNameReqParamName, senderUserName));
            urlParameters.add(new BasicNameValuePair(senderPasswordReqParamName, encryptedPassword));
            urlParameters.add(new BasicNameValuePair(senderReqParamName, sender));
            urlParameters.add(new BasicNameValuePair(mobileNumberReqParamName, countryCode() + mobileNumber));
            urlParameters.add(new BasicNameValuePair(messageTypeParamName, "singlemsg"));
            urlParameters.add(new BasicNameValuePair(messageReqParamName, message));
            urlParameters.add(new BasicNameValuePair("key", genratedhashKey));
            setAdditionalParameters(urlParameters, priority);
            LOGGER.info("impl sms urlParameters = " +  urlParameters.toArray());
            post.setEntity(new UrlEncodedFormEntity(urlParameters, encoding()));
            HttpResponse response = client.execute(post);
            String responseCode = IOUtils.toString(response.getEntity().getContent(), encoding());
            LOGGER.info("impl sms response = " + responseCode);

            return smsErrorCodes.parallelStream().noneMatch(responseCode::startsWith);

        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Error occurred while sending SMS [%s]", e);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Error occurred while sending SMS [%s]", e);
        } catch (ClientProtocolException e) {
            LOGGER.error("Error occurred while sending SMS [%s]", e);
        } catch (IOException e) {
            LOGGER.error("Error occurred while sending SMS [%s]", e);
        }
        return false;
    }

    private void setAdditionalParameters(List<NameValuePair> urlParameters, NotificationPriority priority) {
        if (!extraRequestParams.isEmpty()) {
            for (String extraParm : extraRequestParams) {
                String[] paramNameValue = extraParm.split("=");
                urlParameters.add(new BasicNameValuePair(paramNameValue[0], paramNameValue[1]));
            }
        }

        if (smsPriorityEnabled) {
            urlParameters.add(new BasicNameValuePair(smsPriorityParamName,
                    environment.getProperty(String.format(SMS_PRIORITY_PARAM_VALUE, priority.toString()))));
        }
    }

    private static String MD5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-1");
        byte[] md5 = new byte[64];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        md5 = md.digest();
        return convertedToHex(md5);
    }

    protected String hashGenerator(String userName, String senderId, String content, String secureKey) {
        StringBuilder finalString = new StringBuilder();
        finalString.append(userName.trim()).append(senderId.trim()).append(content.trim()).append(secureKey.trim());
        String hashGen = finalString.toString();
        StringBuilder sb = new StringBuilder();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(hashGen.getBytes());
            byte byteData[] = md.digest();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Error occurred while Hash Generator [%s]", e);
        }
        return sb.toString();
    }

    private static String convertedToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < data.length; i++) {
            int halfOfByte = (data[i] >>> 4) & 0x0F;
            int twoHalfBytes = 0;
            do {
                if ((0 <= halfOfByte) && (halfOfByte <= 9)) {
                    buf.append((char) ('0' + halfOfByte));
                } else {
                    buf.append((char) ('a' + (halfOfByte - 10)));
                }
                halfOfByte = data[i] & 0x0F;
            } while (twoHalfBytes++ < 1);
        }
        return buf.toString();
    }
}
