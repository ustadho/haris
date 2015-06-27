create table m_menu_group_list(
	group_id character varying(10) not null, 
	menu_id bigint not null, 
	primary key(group_id, menu_id)
);
	
alter table m_menu_list  add column urut integer;

create table m_tipe_pelanggan(
	nama varchar (30) primary key, 
	keterangan text
);

insert into m_tipe_pelanggan(nama) values
('KLINIK'),('RESELLER');

create table barang_harga_pelanggan(
	kode_barang varchar(15) not null, 
	tipe_pelanggan varchar(30) not null, 
	harga numeric(12,2), 
	primary key(kode_barang, tipe_pelanggan), 
	foreign key(kode_barang) references barang(item_code),
	foreign key(tipe_pelanggan) references m_tipe_pelanggan(nama)
);


create or replace function fn_get_no_reg()
returns varchar
as
$$
declare
	v_new_no_reg varchar;
begin
	select into v_new_no_reg to_char(current_date, 'yyMM-')||trim(to_char(max(substring(no_reg from '.....$'))::int+1, '00000'))
	from rm_reg where no_reg like to_char(current_date, 'yyMM-')||'%';

	return coalesce(v_new_no_reg, to_char(current_date, 'yyMM-')||'00001');
	
end
$$
language 'plpgsql';


create or replace function fn_tg_reg_ins()
returns trigger
as
$$
begin
if(TG_OP='INSERT') then
	INSERT INTO rm_reg_pasien_data(
		    no_reg, norm, nama, jenis_kelamin, tempat_lahir, tgl_lahir, alamat_domisili, 
		    telepon, hp, nama_keluarga, telp_keluarga, time_ins, user_ins)
	select NEW.no_reg, norm, nama, jenis_kelamin, tempat_lahir, tgl_lahir, alamat_domisili, 
		    telepon, hp, nama_keluarga, telp_keluarga, now(), NEW.user_ins
	from rm_pasien where norm=NEW.norm;
	return NEW;
END IF;

return NULL;
end
$$
language 'plpgsql';

CREATE TRIGGER tg_reg_ins
  AFTER INSERT
  ON rm_reg
  FOR EACH ROW
  EXECUTE PROCEDURE fn_tg_reg_ins();

create or replace function fn_usia(date, date)
returns setof record
as

$$

declare
	v_sampai	alias for $1;
	v_dari		alias for $2;
	rcd 	record;
begin
for rcd in
	select 	extract('year' from age(current_date, v_dari::date)) as tahun, 
	extract('mon' from age(current_date, v_dari::date)) as bulan,
	extract('day' from age(current_date, v_dari::date)) as hari
loop
	return next rcd;
end loop;	
end
--select tahun, bulan, hari from fn_usia(current_date, '1978-03-15'::date) as (tahun double precision, bulan double precision, hari double precision)
$$
language 'plpgsql';


create or replace function fn_tgl_lahir(date, int, int, int)
returns date
as

$$

declare
	v_sampai	alias for $1;
	v_tahun		alias for $2;
	v_bln		alias for $3;
	v_hari		alias for $4;
	rcd 	record;
begin
	return v_sampai - (v_tahun||' years '||v_bln||' months '||v_hari||' days')::interval;	
end
--select fn_tgl_lahir(current_date, 37, 1, 14)
$$
language 'plpgsql';

select * from rm_reg