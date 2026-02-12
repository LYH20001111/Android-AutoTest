#ifndef RSA_H_
#define RSA_H_

#include <stdint.h>

#define MIN_RSA_MODULUS_BITS 508
#define MAX_RSA_MODULUS_BITS 2048
#define MAX_RSA_MODULUS_LENGTH  ((MAX_RSA_MODULUS_BITS + 7) / 8)
#define MAX_RSA_PRIME_BITS      ((MAX_RSA_MODULUS_BITS + 1) / 2)
#define MAX_RSA_PRIME_LENGTH    ((MAX_RSA_PRIME_BITS + 7) / 8)

typedef struct {
        uint32_t bits;                          /* length in bits of modulus */
        uint8_t modulus[MAX_RSA_MODULUS_LENGTH];   /* modulus */
        uint8_t exponent[MAX_RSA_MODULUS_LENGTH];  /* public exponent */
}rsa_pub_key_t;

typedef struct {
        uint32_t bits;                          /* length in bits of modulus */
        uint8_t modulus[MAX_RSA_MODULUS_LENGTH];   /* modulus */
        uint8_t exponent[MAX_RSA_MODULUS_LENGTH];  /* private exponent */
        uint8_t publicExponent[MAX_RSA_MODULUS_LENGTH];    /* public exponent */
        uint8_t prime[2][MAX_RSA_PRIME_LENGTH];            /* prime factors */
        uint8_t primeExponent[2][MAX_RSA_PRIME_LENGTH];    /* exponents for CRT */
        uint8_t coefficient[MAX_RSA_PRIME_LENGTH];         /* CRT coefficient */
}rsa_priv_key_t;

int rsa_public_func(uint8_t *output,      /* output block */
                uint32_t *outputLen,    /* length of output block */
                uint8_t *input,         /* input block */
                uint32_t inputLen,      /* length of input block */
                rsa_pub_key_t *publicKey /* RSA public key */);

int rsa_private_func(uint8_t *output,     /* output block */
                uint32_t *outputLen,    /* length of output block */
                uint8_t *input,         /* input block */
                uint32_t inputLen,      /* length of input block */
                rsa_priv_key_t *privateKey /* RSA private key */);

#endif /* RSA_H_ */
