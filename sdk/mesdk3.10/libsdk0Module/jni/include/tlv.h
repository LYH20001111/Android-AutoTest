#ifndef __TLV_H__
#define __TLV_H__

//#include "platform.h"
//#include "interface.h"

#define is_constructed(byte)        ((byte) & 0x20)
#define is_primitive(byte)          !is_constructed(byte)
#define has_subsequent(byte)        (((byte) & 0x1F) == 0x1F)
#define another_byte_follow(byte)   ((byte) & 0x80)
#define lenlen_exceed_one(byte)     ((byte) & 0x80)


/*    ******    interface.h 已经有下面定义    ******        */
typedef struct tlv {
    /* internal state */
    unsigned char *_tagptr;     /* pointer to 'tag' field */
    unsigned char *_lenptr;     /* pointer to 'len' field */
    unsigned int _len;          /* 'outer' length, specified by user */

    /* parsed information */
    int parent;                /*    recored the parent position */
    int childnum;                /*    the num of children */
    unsigned int tagname;        /*    current tagname */
    unsigned int valuelen;        /*    length of value */
    unsigned char * pvalue;    /*    pointer of value*/
}tlv_t;

/*    deflag defined  */
#define SINGLE_TLVOBJ       0x01    /* it is a single constructed object */
#define STRING_TLVOBJ       0x02    /* it is a tlv object string not coded in a constructed object */
#define DECODE_LEVEL1       0x10    /* just decode the object in level one */


#define TERMINAL_SIZE	360
#define TERMINAL_NUM	128

extern int parse_tlv(unsigned char * ptlvstr, int tlvlen, tlv_t * pobj, int objspace, int deflag);



/*    ******    interface.h 已经有下面定义    ******        */
#define SEARCH_ALL_DESC     0x01    /* search all the descendants */
#define SEARCH_ONLY_SON     0x02    /* search only its sons  */
extern int TlvAdd(const unsigned int unTag, const int nInLen, const uchar *pszInValue, uchar *pszOutTlvBuf, int *pnOutTlvBufLen);
extern int fetch_tlv(int parent,  unsigned int tagname, tlv_t * pobj, int level);
extern int find_dup_tlv(int pos, tlv_t * pobjects);
extern int decode_tag(unsigned char * ptlvstr, int tlvlen, unsigned int *tagname, int objspace);
extern int mpos_clearonetlv(const char *pszName , unsigned int tagname);
extern int mpos_writeonetlv(const char *pszName , tlv_t * pobj);
extern int mpos_readonetlv(const char *pszName , tlv_t * pobj);
extern int mpos_writemacdata(tlv_t * pobj,int num);
extern int mpos_readmacdata(tlv_t * pobj,unsigned char* pdata);
extern int Tlv_Tag2List(unsigned char *psTag, const unsigned int nLen, unsigned int nTagList[], int nSize);
extern int Tlv_Init(void);
#endif /* __TLV_H__ */
