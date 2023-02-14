package org.example;

import net.sf.saxon.s9api.*;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.tika.parser.AutoDetectParser;

import javax.xml.transform.stream.StreamSource;

public class Main {
    public static void main(String[] args) throws SaxonApiException {
        Processor processor = new Processor(false);

        processor.registerExtensionFunction(new ExtensionFunction() {
            @Override
            public QName getName() {
                return new QName("http://example.com/mf/tika", "parse-rtf");
            }

            public SequenceType getResultType() {
                return SequenceType.makeSequenceType(
                        ItemType.ANY_NODE, OccurrenceIndicator.ONE
                );
            }
            @Override
            public SequenceType[] getArgumentTypes() {
                return new SequenceType[]{
                        SequenceType.makeSequenceType(
                                ItemType.STRING, OccurrenceIndicator.ONE)};
            }

            @Override
            public XdmValue call(XdmValue[] xdmValues) throws SaxonApiException {
                try {
                    return parseRtfToHTML2(xdmValues[0].itemAt(0).getStringValue(), processor);
                } catch (IOException | URISyntaxException e) {
                    throw new SaxonApiException(e);
                } catch (SAXException e) {
                    throw new SaxonApiException(e);
                } catch (TikaException e) {
                    throw new SaxonApiException(e);
                }
            }
        });

        XsltCompiler xsltCompiler = processor.newXsltCompiler();

        Xslt30Transformer xslt30Transformer = xsltCompiler.compile(new StreamSource(new File("sheet1.xsl"))).load30();

        XdmValue result = xslt30Transformer.applyTemplates(new StreamSource(new File("sample1.xml")));

        System.out.println(result);
    }

    public static XdmNode parseRtfToHTML(String rtf, Processor processor) throws IOException, SAXException, TikaException, URISyntaxException {
        DocumentBuilder docBuilder = processor.newDocumentBuilder();
        docBuilder.setBaseURI(new URI("urn:from-string"));

        ContentHandler handler = new ToXMLContentHandler();

        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        try (InputStream stream = new ByteArrayInputStream(rtf.getBytes("utf8"))) {
            parser.parse(stream, handler, metadata);
            return docBuilder.build(new StreamSource(new StringReader(handler.toString())));
        } catch (SaxonApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static XdmNode parseRtfToHTML2(String rtf, Processor processor) throws IOException, SAXException, TikaException, URISyntaxException, SaxonApiException {
        DocumentBuilder docBuilder = processor.newDocumentBuilder();
        //docBuilder.setBaseURI(new URI("urn:from-string"));

        //ContentHandler handler = new ToXMLContentHandler();

        BuildingContentHandler handler = docBuilder.newBuildingContentHandler();

        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        try (InputStream stream = new ByteArrayInputStream(rtf.getBytes("utf8"))) {
            parser.parse(stream, handler, metadata);
            return handler.getDocumentNode();//docBuilder.build(new StreamSource(new StringReader(handler.toString())));
        } catch (SaxonApiException e) {
            throw new RuntimeException(e);
        }
    }
}