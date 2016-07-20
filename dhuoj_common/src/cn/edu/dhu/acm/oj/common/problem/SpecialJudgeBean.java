package cn.edu.dhu.acm.oj.common.problem;

import org.jdom.Element;
/**
 *
 * @author wujy
 */
public final class SpecialJudgeBean extends NodeBean {

    public SpecialJudgeBean() {
        super("SpecialJudge", true);
        setLanguage("cpp");
        setFilename("");
    }

    public SpecialJudgeBean(Element element) {
        super(element);
    }

    public String getSourceCode() {
        return super.root.getText();
    }

    public void setSourceCode(String s) {
        super.root.setText(s);
    }

    public String getLanguage() {
        return super.root.getAttributeValue("language");
    }

    public void setLanguage(String s) {
        super.root.setAttribute("language", s);
    }

    public String getFilename() {
        return super.root.getAttributeValue("filename");
    }

    public void setFilename(String s) {
        super.root.setAttribute("filename", s);
    }
    
}
