import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AuthComponent} from './login-register/auth.component';
import {AuthService} from './auth.service';
import {AuthGuard} from './auth-guard.service';
import {AdminGuard} from './admin-guard.service';
import {AuthRoutingModule} from './auth-routing.module';
import {FormsModule} from '@angular/forms';

@NgModule({
  declarations: [
    AuthComponent
  ],
  imports: [
    CommonModule,
    AuthRoutingModule,
    FormsModule
  ],
  providers: [
    AuthService,
    AuthGuard,
    AdminGuard
  ]
})
export class AuthModule { }
