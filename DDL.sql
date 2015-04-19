create table rm_pasien(
	norm varchar(6) primary key,
	nama varchar(100) not null,
	jenis_kelamin varchar(1) not null,
	tempat_lahir varchar,
	tgl_lahir date,
	alamat_domisili varchar(70),
	telepon varchar(30),
	hp varchar(30),
	nama_keluarga varchar(50),
	telp_keluarga varchar(30),
	time_ins timestamp without time zone default now(), 
	user_ins varchar, 
	time_upd timestamp without time zone, 
	user_upd varchar, 
);

create table rm_reservasi(
	id bigserial primary key,
	norm varchar(6), 
	nama varchar(100) not null,
	jenis_kelamin varchar(1) not null,
	tempat_lahir varchar,
	tgl_lahir date,
	alamat_domisili varchar(70),
	telepon varchar(30),
	hp varchar(30),
	nama_keluarga varchar(50),
	telp_keluarga varchar(30)
);

create table rm_reg(
	no_reg varchar(12) primary key, 
	kode_dokter varchar(6),
	norm varchar(6), 
	tanggal date, 
	diagnosa_masuk text,
	alergi text, 
	berat_badan numeric(12,2), 
	mag boolean default false, 
	time_ins timestamp without time zone default now(), 
	user_ins varchar, 
	time_upd timestamp without time zone, 
	user_upd varchar, 
	foreign key (norm) references rm_pasien(norm),
	foreign key (kode_dokter) references dokter(kode_dokter)
);

CREATE OR REPLACE FUNCTION fn_get_new_norm()
  RETURNS character varying AS
$BODY$
DECLARE 
	v_new_code	varchar(13);
BEGIN
	select into v_new_code 
	TRIM(to_char(max(norm)::int +1,'00000') ) 
	from rm_pasien;

return coalesce(v_new_code, '00001');
--select fn_get_new_norm()
END
$BODY$
  LANGUAGE plpgsql VOLATILE;


  INSERT INTO rm_pasien(\n" +
                "            norm, nama, jenis_kelamin, tempat_lahir, tgl_lahir, alamat_domisili, \n" +
                "            telepon, hp, nama_keluarga, telp_keluarga, user_ins)\n" +
                "    VALUES (?, ?, ?, ?, ?, ?, \n" +
                "            ?, ?, ?, ?, ?)
	