package com.game.controller;

import com.game.entity.JsonEntity;
import com.game.model.ServerArea;
import com.game.service.IConfigService;
import com.game.service.IServerAreaService;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * Created by jeniss on 17/5/23.
 */
@RestController
@RequestMapping("/cron")
public class CronController {
    private static final Logger logger = Logger.getLogger(CronController.class);

    @Autowired
    public IServerAreaService serverAreaService;

    @Autowired
    public IConfigService configService;

    @RequestMapping("/crawlingDataToSave.do")
    public JsonEntity crawlingDataToSave() throws IOException {
        List<ServerArea> serverAreaList = serverAreaService.getAll();

        //get url of data
        String gameUrl = configService.getConfigByCode("url_game").getValue();
        String areaUrl = configService.getConfigByCode("url_area").getValue();
        String serverUrl = configService.getConfigByCode("url_server").getValue();
        String typeUrl = configService.getConfigByCode("url_type").getValue();

        String urlPrefix = "http://www.uu898.com/newTrade.aspx?gm=61&c=-3";
        for (ServerArea serverArea : serverAreaList) {
            for (ServerArea child : serverArea.getChildServerAreas()) {
                String urlStr = urlPrefix + "&area=" + serverArea.getCode() + "&srv=" + child.getCode();
                Document document = Jsoup.connect(urlStr).timeout(10000).get();
                Elements contentElement = document.select("div[id=divCommodityLst] ul");
                if (contentElement != null) {
                    for (Element element : contentElement) {
                        logger.info(element.html());
                        logger.info("----------------------------");
                    }
                }
                break;
            }
            break;
        }

        return new JsonEntity();
    }

}
