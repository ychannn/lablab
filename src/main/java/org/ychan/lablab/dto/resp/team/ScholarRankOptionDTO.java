package org.ychan.lablab.dto.resp.team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ychan.lablab.enums.ScholarRankEnum;

/**
 * 学者职级选项（下拉用）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScholarRankOptionDTO {
    private int code;
    private String label;

    public static ScholarRankOptionDTO from(ScholarRankEnum e) {
        return new ScholarRankOptionDTO(e.getCode(), e.getLabel());
    }
}
