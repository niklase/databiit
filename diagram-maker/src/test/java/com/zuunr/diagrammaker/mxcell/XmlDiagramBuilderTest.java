package com.zuunr.diagrammaker.mxcell;

import com.zuunr.json.JsonValue;
import com.zuunr.json.JsonValueFactory;
import org.junit.jupiter.api.Test;

public class XmlDiagramBuilderTest {

    @Test
    void test1() {
        String jsonSchema = """
                {
                    "type": "object",
                    "properties": {
                        "name": {
                            "type": "string"                        
                        }
                    }
                }
                """;
        JsonValue jsonValue = JsonValueFactory.create(jsonSchema);
        String xmlFile = XmlDiagramBuilder.xmlDiagramOf(jsonValue);
        System.out.println("Drawio file: ");
        System.out.println(xmlFile);
    }

    @Test
    void test2() {
        String jsonSchema = """
                {
                    "title": "Person",
                    "type": "object",
                    "properties": {
                        "name": {
                            "type": "string"                        
                        },
                        "age": {
                            "type": "integer"
                        }
                    }
                }
                """;
        JsonValue jsonValue = JsonValueFactory.create(jsonSchema);
        String xmlFile = XmlDiagramBuilder.xmlDiagramOf(jsonValue);
        System.out.println("Drawio file: ");
        System.out.println(xmlFile);
    }

    @Test
    void test3() {
        String jsonSchema = """
                
                {
                    "$defs": {
                        "Person": {
                            "title": "Person",
                            "type": "object",
                            "properties": {
                                "name": {
                                    "type": "string"                                },
                                "age": {
                                    "type": "integer"
                                }
                            }
                        },
                        "Address": {
                            "title": "Address",
                            "type": "object",
                            "properties": {
                                "street": {
                                    "type": "string"
                                }
                            }
                        }
                    }
                }
                """;
        JsonValue jsonValue = JsonValueFactory.create(jsonSchema);
        String xmlFile = XmlDiagramBuilder.xmlDiagramOf(jsonValue);
        System.out.println("Drawio file: ");
        System.out.println(xmlFile);
    }

    @Test
    void test4() {
        String jsonSchema = """
                
                {
                    "$defs": {
                        "Person": {
                            "title": "Person",
                            "type": "object",
                            "properties": {
                                "homeAddress": {
                                    "$ref": "#/$defs/Address"
                                }
                            }
                        },
                        "Address": {
                            "title": "Address",
                            "type": "object",
                            "properties": {
                                "street": {
                                    "type": "string"
                                }
                            }
                        }
                    }
                }
                """;
        JsonValue jsonValue = JsonValueFactory.create(jsonSchema);
        String xmlFile = XmlDiagramBuilder.xmlDiagramOf(jsonValue);
        System.out.println("Drawio file: ");
        System.out.println(xmlFile);
    }

    @Test
    void test5() {
        String jsonSchema = """
                
                {
                    "$defs": {
                        "Person": {
                            "title": "Person",
                            "type": "object",
                            "properties": {
                                "homeAddress": {
                                    "type": "object",
                                    "properties": {
                                        "street": {
                                            "type": "string"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                """;
        JsonValue jsonValue = JsonValueFactory.create(jsonSchema);
        String xmlFile = XmlDiagramBuilder.xmlDiagramOf(jsonValue);
        System.out.println("Drawio file: ");
        System.out.println(xmlFile);
    }

    @Test
    void test6() {
        String jsonSchema = """
                
                {
                    "$defs": {
                        "Person": {
                            "title": "Person",
                            "type": "object",
                            "properties": {
                                "name": {
                                    "type": "string"
                                },
                                "homeAddress": {
                                    "$ref": "#/$defs/Address"
                                },
                                "pets": {
                                    "type": "object",
                                    "properties": {
                                        "petName": {
                                            "type": "string"
                                        }
                                    }
                                }
                            }
                        },
                        "Address": {
                            "title": "Address",
                            "type": "object",
                            "properties": {
                                "street": {
                                    "type": "string"
                                }
                            }
                        }
                    }
                }
                """;
        JsonValue jsonValue = JsonValueFactory.create(jsonSchema);
        String xmlFile = XmlDiagramBuilder.xmlDiagramOf(jsonValue);
        System.out.println("Drawio file: ");
        System.out.println(xmlFile);
    }

}
